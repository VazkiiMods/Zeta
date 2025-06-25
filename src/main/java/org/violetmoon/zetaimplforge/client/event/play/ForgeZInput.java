package org.violetmoon.zetaimplforge.client.event.play;

import net.neoforged.neoforge.client.event.InputEvent;
import org.violetmoon.zeta.client.event.play.ZInput;

public class ForgeZInput implements ZInput {
	public static class MouseButton extends ForgeZInput implements ZInput.MouseButton {
		private final InputEvent.MouseButton.Post e;

		public MouseButton(InputEvent.MouseButton.Post e) {
			this.e = e;
		}

		@Override
		public int getButton() {
			return e.getButton();
		}

		@Override
		public int getAction() {
			return e.getAction();
		}
	}

	public static class Key extends ForgeZInput implements ZInput.Key {
		private final InputEvent.Key e;

		public Key(InputEvent.Key e) {
			this.e = e;
		}

		@Override
		public int getKey() {
			return e.getKey();
		}

		@Override
		public int getAction() {
			return e.getAction();
		}

		@Override
		public int getScanCode() {
			return e.getScanCode();
		}
	}
}