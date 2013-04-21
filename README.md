Introduction
============

I decided to adopt and refine the code as there was no trace of a website
and it had remained untouched since 2000. All credit for legacy structure
(i.e. what was present at the "Initial Commit" stage) goes to
Enrique Campos-Nanez. See ChangeLog.old for old history (i.e. pre-GIT)

This project is hosted at [GitHub](https://github.com/esseks/nscript): refer
to that page for opening tickets, requesting features and contacting
the author (well, the present author, Stefano Sanfilippo - Enrique Campos
should be reachable at the email address at the bottom of this file).

The new icon set is taken from GNOME 3 icons, released under GPL.

Refer to doc/AUTHORS for contact information.

LICENSE
=======

This project is released under BSD-permissive. Exact license terms are contained
in doc/LICENSE


Prebuilts for Windows
=====================
Windows executables for NS2, NAM and DLL for tcl are provided *for convenience
only*. The author did not compile or audit the binaries and makes *NO* warranty.
See README.prebuilts for specific information.

Building
========

Files are installed in the `./nscript` directory at top level and prebuilt
zip is placed there too. The build system does not support root installation,
but I am definitely going to add that.

Prerequisites
-------------

This is a Java application, so you will need a JRE/JDK installed.

**IMPORTANT**: *you must have JDK 1.5 or more recent (recommended >= 1.6)*.
I *strongly* recommend JDK 1.7, first because it fixes many security updates,
and secondly because I am planning to refactor unsafe code and 1.7 additions
might come into play.

You must have Apache [Ant](http://ant.apache.org/) installed.
Most GNU/Linux distributions ship it, so you should use your package manager.
Windows and Mac users will find prebuilt binaries on ANT website.

You also need [Proguard](http://proguard.sourceforge.net/) installed.
Adjust the path to `proguard.jar` in the first lines of build.xml to reflect
your current installation.

JUST DO IT!
-----------

Issue:

    ant clean prebuilt

and you will get a nice `nscript-*.zip` file ready to ship, unpack, send &co.

If you'd rather have your files installed in the top level folder, use:

    ant clean install

The build process should last less than a minute (much less).

Note for *nix lovers
--------------------

The build system is integrated with a Makefile, so that the installation
boils down to the familiar:

    make
    make install

*NOTE*: that's only a convenience, you will need Apache Ant and a JDK anyway.

Caveats
=======

I've been doing a lot of refactoring since the codebase was old (see Git log).
Anyway, there are some rough edges that I don't think it's worth addressing,
as they would require a massive redesign of NScript (which supports an outdated
version of NS) and I'd rather rewrite it from scratches for NS3 rather
than refigure class layout (maybe it will happen in the future, but
do not count on it).

Spefically, the distribution between *Object and Tcl* classes/interfaces
is incorrect, since it forces to do unchecked casts to subclass
in order to retrieve specific attributes. There are big `switch()` trees using
"type" flags which should have been implemented as a subclassing mechanism
instead.


Notes applying to the original archive as downloaded from the Internet.
=======================================================================

* The original zipfile was downloaded from:


* I found no reference webpage for this software and last update dates back to
12 years ago. First step was to rationalize source code and move to JDK 1.7
compliance (e.g. all source files were moved in a package)

* All dot files (.DS_store &co.) have been removed: obviously they
weren't part of the source - they were rather unintentional leaves.

* The package contained no license (seriously): I've assigned a BSD-permissive
license to the package. The original author (Enrique Campos-Nunez) agreed
with this choice.

* Old docs were stripped as outdated and so were the javadocs (and all of the
generated resources).

* For all modifications and fixes please refer to the git log.

--Stefano Sanfilippo
