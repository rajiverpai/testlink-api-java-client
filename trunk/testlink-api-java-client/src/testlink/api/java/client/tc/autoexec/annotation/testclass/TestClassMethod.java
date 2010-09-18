/*
 * Daniel R Padilla
 *
 * Copyright (c) 2009, Daniel R Padilla
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package testlink.api.java.client.tc.autoexec.annotation.testclass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class TestClassMethod implements TestClassMember {
	private Method method;
	public TestClassMethod(Method method) {
		this.method= method;
	}
	
	public String getName() {
		return method.getName();
	}
	
	public boolean isShadowedBy(List members) {
		return false;
	}
	
	public Annotation[] getAnnotations() {
		return method.getAnnotations();
	}
}
