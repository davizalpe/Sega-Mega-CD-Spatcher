Sega Mega CD Snatcher Patcher
=============================

This is project that creates a CLI and Desktop application for path the iso file from Snatcher videogame and replace original files for modified or translated file using [Sega-CD-Snatcher-Language-Patcher](https://github.com/3lm4dn0/Sega-CD-Snatcher-Language-Patcher).

Install
-------

* Install Open JDK Java or official Oracle versions 1.7 or highter.

* Rename /src/main/encryption/Encrypt.java.default to /src/main/encryption/Encrypt.java
Change the algo, passphrase and IV for your election.

* (old school) To create jar File with command line
Rename MakeJar.sh.default to MakeJar.sh.
Change data files path that you want to replace.

(Optional) Generate your Java Cert:
$ keytool -genkey -keyalg RSA -alias selfsigned -keystore  -storepass password -validity 360 -keysize 2048 
NOTE: If you will not use Java Certification, comment this line 'jarsigner -keystore spatcherKeystore spatcher.jar spatcherkey'

(Optional) Sign with your private key with GPG:
NOTE: if you will not use GPG, comment this line 'gpg --output spatcher.jar.sig --sign spatcher.jar'

$ chmod +x MakeJar.sh
$ ./MakeJar.sh

Usage
-----
require 'Java 1.7 or highter. An iso file NTSC or PAL from Sega/Mega Snatcher.'

* CLI:
$ java -jar spatcher.jar -T orignal.iso new.iso [--removeCensure|-r]

* Desktop:
$ java -jar spatcher.jar 
or just clic on the jar file.

License
-------

The source code is licensed under the GPL version 3 or later
(see COPYING).

This file is part of Spatcher.

    Spantcher is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Spatcher is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Spatcher.  If not, see <http://www.gnu.org/licenses/>.

