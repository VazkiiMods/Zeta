# Zeta
Modular Mod development framework based on Quark's Module System.

Read the pitch and design idea here: https://forum.violetmoon.org/d/78-project-zeta-aka-quark-on-fabric-real

This mod makes use of the [speedy-math](https://github.com/stefan-zobel/speedy-math) by stefan-zobel, licensed under the Apache 2.0 License.  

## Release Process

Zeta's release process is mostly automated. Here's the steps:

1. Pull master so you're up to date, make sure everything is committed
2. Run `git tag -a release-<mc_version>-<build_number>`. If you don't know or remember what those are, look at `build.properties`.
	* Make sure to use the full Minecraft version (use `1.20.1` instead of `1.20`)
3. In the editor that pops up, write the changelog
4. In `build.properties`, increment the build_number by one for the next version. Commit this.
5. Push master and the release tag: `git push origin master release-<mc_version>-<build_number>`
6. Shortly after, the mod should be automatically uploaded to GitHub's release tab, Modrinth, and CurseForge.

## Signing
Releases are signed with the Violet Moon signing key, see [this
page](https://github.com/VazkiiMods/.github/blob/main/security/README.md) for information
about how to verify the artifacts.