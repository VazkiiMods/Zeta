import os
import re
from jproperties import Properties

def increment_patch(version: str) -> str:
    parts = version.strip().split('.')
    if len(parts) != 3:
        raise ValueError(f"Version must be in x.y.z format!!! Not the following: {version}")
    major, minor, patch = map(int, parts)
    patch += 1
    return f"{major}.{minor}.{patch}"

def main():
	properties = Properties()
	with open('gradle.properties', 'rb') as f:
	    properties.load(f , "utf-8")

	mc_version, mcv_meta = properties['minecraft_version']
	version, v_meta = properties['mod_version']

	print('MC Version:', mc_version)
	print('Version:', version)

	changelog = ''
	with open('changelog.txt', 'r') as f:
		content = f.read()
		content = content.replace('"', '\'')
		lines = content.splitlines()
		for line in lines:
			changelog = changelog + '-m "'+line+'" '

	tag_success = os.system('git tag -a release-{}-{} {}'.format(mc_version, version, build_number, changelog))

	if tag_success != 0:
		print('Failed to create tag')
		return
	else :
		print('Created tag')

    new_version = increment_patch(version)
    build['mod_version'] = new_version
        

	with open("gradle.properties.properties", "wb") as f:
	    properties.store(f, encoding="utf-8")

	os.system('git commit -a -m build')
	os.system('git push origin main release-{}-{}'.format(mc_version, version))

if __name__ == '__main__':
	main()