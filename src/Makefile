#
# Copyright (C) 2012 Stefano Sanfilippo.
# See COPYING at top level for more information.
#

all:
	ant jar
clean:
	ant clean
doc:
	ant javadoc
install:
	install -d resources/pixmaps/ ../bin/pixmaps
	install -d resources/lib/ ../bin/lib
	install -d resources/settings/ ../bin/settings
	install resources/nscript.{bat,sh} ../bin
	install build/nscript.jar ../bin

