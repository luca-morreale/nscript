Name:     nscript
Version:  1.1.1
Release:  2%{?dist}
Summary:  NS2 visual Network Editor

License:  BSD modern
URL:      https://github.com/esseks/nscript

Requires: ns2 nam

%description
Visual interface for building ns-tcl scripts. Specifically, it allows you to:

 * Build the topology by drawing it.
 * Configure elements in the network, such as transport agents and applications.
 * Handle event scheduling for your simulation script.
 * Trace and plot simulation information (with gnuplot).

%prep


%build


%install
ant clean install \
    -buildfile ${srcdir}/%{name}-%{version}/src/build.xml \
    -Dinstall.dir=$RPM_BUILD_ROOT \
    -Dinstall.lib.dir=$RPM_BUILD_ROOT/usr/share/%{name} \
    -Dinstall.conf.dir=$RPM_BUILD_ROOT/usr/share/%{name} \
    -Dinstall.jar.dir=$RPM_BUILD_ROOT/usr/share/%{name} \
    -Dinstall.bin.dir=$RPM_BUILD_ROOT/usr/bin \
    -Dinstall.doc.dir=$RPM_BUILD_ROOT/usr/share/doc/%{name} \
    -Dinstall.examples.dir=$RPM_BUILD_ROOT/usr/share/doc/%{name}

%files
%doc


%changelog
