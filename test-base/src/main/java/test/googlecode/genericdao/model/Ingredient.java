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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Ingredient {
	private long ingredientId;
	private String name;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(long id) {
		this.ingredientId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Ingredient))
			return false;
		Ingredient i = (Ingredient) o;

		return (getName() == null) ? i.getName() == null : getName().equals(i.getName());
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (name == null ? 0 : name.hashCode());
		return hash;
	}

	public String toString() {
		return "Ingredient::ingredientId:" + ingredientId + ",name:" + name;
	}

}
