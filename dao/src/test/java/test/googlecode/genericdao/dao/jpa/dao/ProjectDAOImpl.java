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
package test.googlecode.genericdao.dao.jpa.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import test.googlecode.genericdao.model.Person;
import test.googlecode.genericdao.model.Project;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.SearchUtil;
import com.googlecode.genericdao.search.Sort;

/**
 * <p>
 * Examples of overriding and adding methods to a DAO.
 * 
 * <p>
 * {@link #findProjectsForMember(Person)} and
 * {@link #getProjectsForMemberSearch(Person)} are two alternative ways of
 * providing pre-defined searches. <code>findProjectsForMember()</code> is
 * simpler to use, but less flexible. <code>getProjectsForMemberSearch</code>
 * requires the code where it is used to make another call to actually get the
 * search result; however, it has the advantage of ultimate flexibility because
 * any number of other options can be specified on the search before it is
 * called.
 * 
 * @author dwolverton
 * 
 */
public class ProjectDAOImpl extends BaseDAOImpl<Project, Long> implements ProjectDAO {
	public List<Project> findProjectsForMember(Person member) {
		if (member == null)
			return new ArrayList<Project>(0);

		return search(getProjectsForMemberSearch(member));
	}

	public Search getProjectsForMemberSearch(Person member) {
		return new Search(Project.class).addFilterSome("members", Filter.equal("id", member.getId()));
	}

	@Override
	public List<Project> search(ISearch search) {
		return super.search(prepareSearch(search));
	}

	@Override
	public SearchResult<Project> searchAndCount(ISearch search) {
		ISearch s = prepareSearch(search);
		return super.searchAndCount(s);
	}

	@Override
	public Project searchUnique(ISearch search) {
		return super.searchUnique(prepareSearch(search));
	}

	/**
	 * Replaces references to "duration", which is a calculated value with
	 * "inceptionYear" and adjusts the comparison values and sort directions
	 * based on that change.
	 */
	private ISearch prepareSearch(ISearch search) {
		// Replace any sort on "duration" with a sort on "inceptionYear" in the
		// opposite order.
		List<Sort> sorts = SearchUtil.walkList(search.getSorts(), new SearchUtil.ItemVisitor<Sort>() {
			@Override
			public Sort visit(Sort sort) {
				if ("duration".equals(sort.getProperty())) {
					return new Sort("inceptionYear", !sort.isDesc());
				} else {
					return sort;
				}
			}
		}, false);

		// Replace any filters on "duration" with the corresponding filter on
		// "inceptionYear". Adjust the filter value accordingly.
		List<Filter> filters = SearchUtil.walkFilters(search.getFilters(), new SearchUtil.FilterVisitor() {
			@Override
			public Filter visitAfter(Filter filter) {
				if ("duration".equals(filter.getProperty())) {
					Filter f = null;
					int thisYear = Calendar.getInstance().get(Calendar.YEAR);

					switch (filter.getOperator()) {
					case Filter.OP_LESS_OR_EQUAL:
						f = Filter.greaterOrEqual("inceptionYear", null);
						break;
					case Filter.OP_GREATER_OR_EQUAL:
						f = Filter.lessOrEqual("inceptionYear", null);
						break;
					case Filter.OP_LESS_THAN:
						f = Filter.greaterThan("inceptionYear", null);
						break;
					case Filter.OP_GREATER_THAN:
						f = Filter.lessThan("inceptionYear", null);
						break;
					case Filter.OP_EQUAL:
						f = Filter.equal("inceptionYear", null);
						break;
					case Filter.OP_IN:
						List<Integer> valueList = new ArrayList<Integer>();

						if (filter.getValue() instanceof Collection) {
							for (Object value : (Collection<?>) filter.getValue()) {
								valueList.add(thisYear - ((Number) value).intValue());
							}
						} else if (filter.getValue() instanceof Object[]) {
							for (Object value : (Object[]) filter.getValue()) {
								valueList.add(thisYear - ((Number) value).intValue());
							}
						}
						return Filter.in("inceptionYear", valueList);
					default:
						return filter;
					}

					f.setValue(thisYear - ((Number) filter.getValue()).intValue());
					return f;
				}
				return super.visitAfter(filter);
			}
		}, false);

		if (sorts == search.getSorts() && filters == search.getFilters()) {
			return search;
		} else {
			return SearchUtil.shallowCopy(search).setFilters(filters).setSorts(sorts);
		}
	}
}
