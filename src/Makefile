#
# Copyright (C) 2012 Stefano Sanfilippo.
# See COPYING at top level for more information.
#

all:
	ant jar

clean:
	ant clean
	rm -rf ../bin

doc:
	ant javadoc

install:
	mkdir -p ../bin/pixmaps
	mkdir -p ../bin/lib
	mkdir -p ../bin/settings
	mkdir -p ../bin/doc
	install -m 0644 resources/pixmaps/*.png ../bin/pixmaps
	install -m 0644 resources/lib/*.lib ../bin/lib
	install -m 0644 resources/settings/{deflibs,environment} ../bin/settings
	install -m 0755 resources/nscript.{bat,sh} ../bin
	install -m 0644 build/nscript.jar ../bin
	install -m 0644 ../README{,.updates}.md ../bin/doc
	install -m 0644 ../COPYING ../bin/doc

