/* Copyright 2013 David Wolverton
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.googlecode.genericdao.model;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OrderColumn;

@Entity
@DiscriminatorValue("1")
public class LimbedPet extends Pet {
	private List<String> limbs;
	private boolean hasPaws;

	@ElementCollection
	@OrderColumn(name = "idx")
	public List<String> getLimbs() {
		return limbs;
	}

	public void setLimbs(List<String> limbs) {
		this.limbs = limbs;
	}

	public boolean isHasPaws() {
		return hasPaws;
	}

	public void setHasPaws(boolean hasPaws) {
		this.hasPaws = hasPaws;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (limbs == null) {
			sb.append("null");
		} else {
			sb.append("[");
			for (String limb : limbs) {
				sb.append(limb);
			}
			sb.append("]");
		}
		return "LimbedPet::super:{" + super.toString() + "},hasPaws:" + hasPaws + ",limbs:" + sb.toString();
	}

}
