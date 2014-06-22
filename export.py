import zipfile
import glob, os
import sys
import re 
import shutil
	
changelog = '['
first = True
isDev = False
isSecret = False
while True:
	change = input("Add an updated feature: ")
	if change == "done":
		break
	elif change == "secret":
		isSecret = True;
		break
	elif change == "dev":
		isDev = True;
		break
	elif change == "exit":
		sys.exit(1)
	else:
		if not first:
			changelog += ','
		changelog += '"' + change + '"'
		first = False
changelog += ']'				
				

targetfolder = "../../public/"
if not isDev:
	max_version = 0
	for name in glob.glob(targetfolder + "StevesFactoryManagerA*.jar"):
		shortname = os.path.basename(name)
		version = shortname[21:-4]
		try:
			version=int(version)
		except:
			continue
		max_version = max(max_version, version)					
	max_version += 1
	alpha = str(max_version)
	version_string = "A" + alpha;				
else:
	version_string = "DEV";	
	

	

#set version
gen_path = "src/main/java/vswe/stevesfactory/GeneratedInfo.java";

info = open(gen_path, "r")
content = info.read()
info.close()	

content = re.sub('/\*@v\*/"((DEV)|(A[0-9]+[a-z]?))";', '/*@v*/"' + version_string + '";', content)

if isDev:
	inDevStr = "true"
else:
	inDevStr = "false"

content = re.sub('/\*@d\*/(true|false);', '/*@d*/' + inDevStr + ';', content)

info = open(gen_path, "w")
info.write(content)
info.close()
#end set version	

#set version gradle
gradle_path = "build.gradle";

info = open(gradle_path, "r")
content = info.read()
info.close()	

content = re.sub('\nversion = ".*?"', '\nversion = "' + version_string + '"', content)

info = open(gradle_path, "w")
info.write(content)
info.close()
#end set version gradle	
	
	
	
os.system("build.LNK")


src	= "build/libs/StevesFactoryManager-" + version_string + ".jar"
target = targetfolder + "StevesFactoryManager" + version_string + ".jar"


shutil.copyfile(src, target)


if not isDev:
	download_path = targetfolder + "DownloadInfoFactory.js";
	info = open(download_path, "r")
	content = info.read()
	info.close()

	prefix = ''
	if isSecret:
		prefix = '//'
	
	content = re.sub('//@Expand', '//@Expand\n' + prefix + 'addVersionA("' + alpha + '", ' + changelog + ');', content)

	info = open(download_path, "w")
	info.write(content)
	info.close()
	

			
