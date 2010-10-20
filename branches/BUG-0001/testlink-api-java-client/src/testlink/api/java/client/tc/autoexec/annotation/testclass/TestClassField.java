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
import java.lang.reflect.Field;
import java.util.List;

public class TestClassField implements TestClassMember {
	private Field field;
	public TestClassField(Field field) {
		this.field = field;
	}
	
	public String getName() {
		return field.getName();
	}
	
	public boolean isShadowedBy(List members) {
		return false;
	}
	
	public Annotation[] getAnnotations() {
		return field.getAnnotations();
	}
}
