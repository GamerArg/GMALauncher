GMA Launcher
===================

## What is the GMA Launcher?
The GMA Launcher is a Minecraft launcher designed to install the current version of Minecraft that GamerArg servers run. It's based purelly on TechnicLauncher wich can be founded on the [Technic Platform][Homepage] page. This launcher, as the original, can only be used by premium Minecraft users.

## The License
The Technic Launcher is licensed under the [GNU General Public License Version 3][License]. Please see the `LICENSE.txt` file for details.

Copyright (c) 2014 GamerArg

## Getting the Source
The latest and greatest source can be found here on [GitHub][Source].

## Compiling the Source
Technic Launcher uses Maven to handle its dependencies.

* Install [Maven 3](http://maven.apache.org/download.html)
* Checkout this repo and run: `mvn clean package`
* To compile an `exe` on a non-Windows platform, add: `-P package-win` to the previous goals.

[Homepage]: http://www.technicpack.net
[License]: http://www.gnu.org/licenses/gpl-3.0.txt
[Source]: https://github.com/ramaroberto/GMALauncher
