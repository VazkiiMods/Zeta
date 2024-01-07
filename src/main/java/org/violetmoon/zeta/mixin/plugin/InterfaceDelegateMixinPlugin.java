package org.violetmoon.zeta.mixin.plugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.util.Annotations;

import java.util.*;

/**
 * Very little safety checking is done. Use wisely or not at all.
 *
 * This is so I can inject into interface methods. God save my soul. - Wire
 */
public class InterfaceDelegateMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		// NO-OP
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		Multimap<String, MethodReference> targets = HashMultimap.create();

		ClassNode mixinClass = mixinInfo.getClassNode(0);

		String thisOwner = targetClassName.replace(".", "/");


		AnnotationNode delegateInterface = Annotations.getVisible(mixinClass, DelegateInterfaceMixin.class);
		if (delegateInterface != null && (mixinClass.access & Opcodes.ACC_INTERFACE) != 0) {
			String callInvoker = Annotations.<Type>getValue(delegateInterface, "delegate").getInternalName();

			List<AnnotationNode> delegatedMethods = Annotations.getValue(delegateInterface, "methods");
			for (AnnotationNode delegate : delegatedMethods) {
				List<String> targetMethods = Annotations.getValue(delegate, "target", false);
				String delegateMethod = Annotations.getValue(delegate, "delegate");
				String desc = Annotations.getValue(delegate, "desc");

				for (String targetName : targetMethods)
					targets.put(targetName, new MethodReference(delegateMethod, Descriptor.tokenize(desc)));
			}

			for (MethodNode method : targetClass.methods) {
				String methodName = method.name + method.desc;
				if (!targets.containsKey(methodName))
					methodName = method.name;

				if (targets.containsKey(methodName)) {
					Descriptor targetDescriptor = Descriptor.tokenize(method.desc);
					int opcode = switch (targetDescriptor.ret) {
						case "V" -> Opcodes.RETURN;
						case "I", "S", "C", "B", "Z" -> Opcodes.IRETURN;
						case "J" -> Opcodes.LRETURN;
						case "F" -> Opcodes.FRETURN;
						case "D" -> Opcodes.DRETURN;
						default -> Opcodes.ARETURN;
					};

					Map<InsnNode, InsnList> transformations = new HashMap<>();

					Collection<MethodReference> nodes = targets.get(methodName);
					for (int i = 0; i < method.instructions.size(); i++) {
						AbstractInsnNode node = method.instructions.get(i);
						if (node instanceof InsnNode insnNode && node.getOpcode() == opcode) {
							buildTransforms(callInvoker, thisOwner, method, targetDescriptor, insnNode, nodes, transformations);
						}
					}

					for (InsnNode transformationTarget : transformations.keySet())
						method.instructions.insertBefore(transformationTarget, transformations.get(transformationTarget));
				}

			}
		}
	}

	private void buildTransforms(String callInvoker, String owner, MethodNode target, Descriptor targetDescriptor, InsnNode returnNode, Collection<MethodReference> transformers, Map<InsnNode, InsnList> transformations) {
		String ownerType = "L" + owner + ";";

		boolean isStatic = (target.access & Opcodes.ACC_STATIC) != 0;

		for (MethodReference transformer : transformers) {
			Descriptor transformerDescriptor = transformer.descriptor;

			InsnList transformation = foldTransformerParams(ownerType, isStatic, targetDescriptor, transformerDescriptor);
			if (transformation != null) {
				transformation.add(new MethodInsnNode(Opcodes.INVOKESTATIC, callInvoker, transformer.name, transformer.descriptor.toString()));

				if (transformations.containsKey(returnNode))
					transformations.get(returnNode).add(transformation);
				else
					transformations.put(returnNode, transformation);
			}
		}
	}

	/**
	 * Returns the VarInsnNode sequence that loads the parameters for the injector.
	 *
	 * The first parameter must be the same as the return type of the injector and the target.
	 * If the target method is not static, you may optionally include the receiver class as a parameter type next.
	 * You may then include as many of the parameters of the method, in order, as you wish. Objects may be subclassed.
	 */
	private InsnList foldTransformerParams(String ownerType, boolean isStatic, Descriptor target, Descriptor transformer) {
		if (!target.ret.equals(transformer.ret))
			return null; // FAIL: Types don't match!

		if (transformer.params.length == 0 || !transformer.params[0].equals(target.ret))
			return null; // FAIL: First argument isn't the same as return type!

		if (transformer.params.length == 1)
			return new InsnList(); // SUCCEED: The transformer is a unary function, so no other loads are necessary!

		InsnList localVarsToLoad = new InsnList();

		boolean firstWasThis = false;

		int lvtIndex = isStatic ? 0 : 1; // Nonstatic methods have `this` in local variable 0.

		for (int i = 0; i < transformer.params.length - 1; i++) {
			String transformerParam = transformer.params[i + 1]; // What type are we considering in the MIXIN method?

			if (i == 0 && !isStatic && transformerParam.equals(ownerType)) { // The first parameter after the return value was the receiver type, so we need to load `this`.
				firstWasThis = true;
				localVarsToLoad.add(new VarInsnNode(Opcodes.ALOAD, 0)); // Loads `this`.
			} else {
				int targetIndex = firstWasThis ? i - 1 : i; // What type are we considering in the TARGET method?
				if (targetIndex >= target.params.length)
					return null; // FAIL: Requested more parameters than the target method had!

				if (transformerParam.charAt(0) == target.params[targetIndex].charAt(0)) { // Do the variable types (generally) match? Fuzziness is allowed in objects, in case that's necessary.
					int opcode = switch (transformerParam) {
						case "I", "S", "C", "B", "Z" -> Opcodes.ILOAD;
						case "J" -> Opcodes.LLOAD;
						case "F" -> Opcodes.FLOAD;
						case "D" -> Opcodes.DLOAD;
						default -> Opcodes.ALOAD;
					};

					localVarsToLoad.add(new VarInsnNode(opcode, lvtIndex++)); // If so, load the next parameter.

					if (opcode == Opcodes.LLOAD || opcode == Opcodes.DLOAD)
						lvtIndex++; // Some types, namely longs and doubles, take up two slots in the local variable table.
				} else {
					return null; // FAIL: Parameter types didn't match up!
				}
			}
		}

		return localVarsToLoad; // SUCCEED: Parameter types all checked out, and we're ready to inject!
	}

	private record MethodReference(String name, Descriptor descriptor) {
		// NO-OP
	}

	private record Descriptor(String[] params, String ret) {
		private static final String END_TOKEN = "ISCBZJFD";

		@Override
		public String toString() {
			StringBuilder out = new StringBuilder("(");
			for (String param : params)
				out.append(param);
			out.append(')');
			out.append(ret);
			return out.toString();
		}

		private static Descriptor tokenize(String desc) {
			if (desc.length() < 3 || desc.charAt(0) != '(')
				return new Descriptor(new String[0], "V");
			List<String> parameters = new ArrayList<>();
			String returnType = "V";
			int pointer = 1;
			char charAt;
			boolean parsingReturnType = false;
			boolean parsingClassType = false;

			StringBuilder collected = new StringBuilder();

			while (pointer < desc.length()) {
				charAt = desc.charAt(pointer++);

				if ((!parsingClassType && END_TOKEN.indexOf(charAt) >= 0) || (parsingClassType && charAt == ';')) {
					collected.append(charAt);
					if (parsingReturnType)
						returnType = collected.toString();
					else
						parameters.add(collected.toString());
					parsingClassType = false;
					collected = new StringBuilder();
				} else if (charAt == ')') {
					parsingReturnType = true;
				} else {
					if (charAt == 'L')
						parsingClassType = true;
					collected.append(charAt);
				}
			}

			return new Descriptor(parameters.toArray(new String[0]), returnType);
		}
	}

}
