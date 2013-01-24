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
# rm -rf $RPM_BUILD_ROOT
# make install DESTDIR=$RPM_BUILD_ROOT

%files
%doc


%changelog
