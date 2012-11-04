NOTICE
======

The lines below refer to the original version of the software and have been
left for documentation purpose. Refer to `README.updates.md` for recent
information. Eventually, the two files will be merged.

The new build system is integrated with a Makefile, so that the installation
boils down to the familiar:

    make
    make install

I decided to adopt and refine the code as there was no trace of a website
and it had remained untouched since 2000. All credit for legacy structure
(i.e. what was present at the "Initial Commit" stage) goes to
Enrique Campos-Nanez.

This project is hosted at [GitHub](https://github.com/satufk/nscript): refer
to that page for opening tickets, requesting features and contacting
the author (well, the present author, Stefano Sanfilippo - Enrique Campos
should be reachable at the email address at the bottom of this file).

The new icon set is taken from GNOME 3 icons, released under GPL.


nscript-1.0
===========

Visual interface for building ns-tcl scripts. Specifically, it allows you to:

* Build the topology by drawing it.
* Configure elements in the network, such as transport 
  agents, and applications.
* Handle event scheduling for your simulation script.
* Trace and plot simulation information (you'll need gnuplot).

The information on how to add classes to the graphical environment is
detailed in the CUSTOMIZING file under the doc directory. In the same a
directory the file formats for a NS library is described, as well as
the VisualNS Documentation and Tutorial.

To make and run this application you'll need jdk1.3, and ns2.1b7 installed,
to use it. Move to the /src directory, and do a "make all".

December, 2000

Version 1.0.1
=============

* Corrects some bugs in the object browser, open, save, and close behavior.

March, 2001

Version 1.0.2
=============

* Code documentation.
* XML Formatting for input/output/file formats.
* Procedure interface for objects.
* Scheduler view.


> Enrique Campos-Nanez
> Department of Systems Engineering
> University of Virginia
> 
> e-mail: ec3z@virginia.edu
> http://www.people.virginia.edu/~ec3z

> Stefano Sanfilippo
> Department of Computer Science
> Politecnico di Milano
> 
> e-mail: a dot. little dot coder at gmail dot. com.

