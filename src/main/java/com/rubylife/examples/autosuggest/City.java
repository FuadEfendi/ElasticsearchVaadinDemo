/*
 * Licensed to Tokenizer under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Tokenizer licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.rubylife.examples.autosuggest;

import java.io.Serializable;
import java.util.Comparator;

public class City  implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;

	private int profileCount;

	public int getProfileCount() {
		return profileCount;
	}

	public void setProfileCount(int profileCount) {
		this.profileCount = profileCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public static class NameComparator implements Comparator<City> {

		@Override
		public int compare(City o1, City o2) {
			return o1.name.compareToIgnoreCase(o2.name);
		}
	}

	public static class NameComparatorDescending implements Comparator<City> {

		@Override
		public int compare(City o1, City o2) {
			return o2.name.compareToIgnoreCase(o1.name);
		}
	}

	public static class ProfileCountComparator implements Comparator<City> {

		@Override
		public int compare(City o1, City o2) {
			return Integer.compare( o1.profileCount, o2.profileCount);
		}
	}

	public static class ProfileCountComparatorDescending implements Comparator<City> {

		@Override
		public int compare(City o1, City o2) {
			return Integer.compare( o2.profileCount, o1.profileCount);
		}
	}

}
