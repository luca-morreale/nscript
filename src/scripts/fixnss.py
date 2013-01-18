#! /usr/bin/python
#
# Copyright (C) 2012 Stefano Sanfilippo.
# See COPYING in the main source package for more information
#

from __future__ import with_statement
import sys

# Ugly ugly trick to give us compatibility both with Py2 and Py3k
try:
    import cStringIO as StringIO
except ImportError:
    try:
        import StringIO
    except ImportError:
        import io as StringIO


FLAG = ['DropTail', 'RED', 'CBQ', 'FQ', 'SFQ', 'DRR']


def fix(filename, overwrite=False):
    """Will append a `Off` flag into each `(Duplex|Simplex)` Link declaration.
    Needed because the old file format was updated to include
    Queue Visualization. Converted file will be saved to ${somename}.new.nss

    Args:
        filename: the name of the file to be converted.
        overwrite: will overwrite input file if `True`.
    Returns:
        None
    """
    with StringIO.StringIO() as buffer:
        with open(filename, 'rt') as sourcefile:
            ready = steady = False
            for line in sourcefile:
                buffer.write(line)
                if line[:-1] in FLAG:
                    ready = True
                if ready and not steady:
                    steady = True
                elif ready and steady:
                    buffer.write('Off\n')
                    ready = steady = False
        if not overwrite:
            filename = filename.replace('.nss', '.new.nss')
        with open(filename, 'wt') as sourcefile:
            sourcefile.write(buffer.getvalue())


def main():
    filenames = sys.argv[1:]
    if filenames:
        for filename in filenames:
            print ('Converting %s' % filename)
            fix(filename)
    else:
        print('Usage: %s file1.nss [file2.nss [...]]' % sys.argv[0])
        sys.exit(0)


if __name__ == '__main__':
    main()
