/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.equinox.resolver.tests;

import org.osgi.framework.*;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
	public static String BUNDLE_RESOLVER = "org.eclipse.equinox.resolver"; //$NON-NLS-1$

	private static Activator plugin;
	private static BundleContext context;

	private ServiceTracker packageAdminTracker;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bc) throws Exception {
		plugin = this;
		packageAdminTracker = new ServiceTracker(bc, PackageAdmin.class.getName(), null);
		packageAdminTracker.open();
		Activator.context = bc;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bc) throws Exception {
		if (packageAdminTracker != null) {
			packageAdminTracker.close();
			packageAdminTracker = null;
		}
		plugin = null;
	}

	private static Activator getDefault() {
		return plugin;
	}

	public static synchronized BundleContext getBundleContext() {
		return context;
	}

	public static synchronized Bundle getBundle(String symbolicName) {
		PackageAdmin packageAdmin = Activator.getDefault().getPackageAdmin();
		if (packageAdmin == null)
			return null;

		Bundle[] bundles = packageAdmin.getBundles(symbolicName, null);
		if (bundles == null)
			return null;
		for (int i = 0; i < bundles.length; i++) {
			if ((bundles[i].getState() & (Bundle.INSTALLED | Bundle.UNINSTALLED)) == 0) {
				return bundles[i];
			}
		}
		return null;
	}

	private PackageAdmin getPackageAdmin() {
		if (packageAdminTracker == null) {
			return null;
		}
		return (PackageAdmin) packageAdminTracker.getService();
	}

}
