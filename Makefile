#
# Copyright (C) 2012 Stefano Sanfilippo.
# See COPYING at top level for more information.
#
#
# This is provided only for *nix convenience, real
# build is handled by Apache Ant
#

all:
	ant prebuilt

clean:
	ant clean

prebuilt:
	ant prebuilt

doc:
	ant javadoc

install:
	ant install
