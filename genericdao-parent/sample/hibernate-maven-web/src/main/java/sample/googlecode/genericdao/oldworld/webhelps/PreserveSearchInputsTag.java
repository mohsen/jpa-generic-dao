package sample.googlecode.genericdao.oldworld.webhelps;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * A tag that copies search parameters from the request to hidden form inputs.
 * Thus the search parameters will again be passed in the next request when the
 * form is submitted.
 * 
 * @author dwolverton
 * 
 */
public class PreserveSearchInputsTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	boolean includeSorts;
	boolean includeFilters;
	boolean includePaging;

	@Override
	public void setPageContext(PageContext pageContext) {
		includeFilters = true;
		includeSorts = true;
		includePaging = true;
		super.setPageContext(pageContext);
	}

	public void setIncludeSorts(boolean includeSorts) {
		this.includeSorts = includeSorts;
	}

	public void setIncludeFilters(boolean includeFilters) {
		this.includeFilters = includeFilters;
	}

	public void setIncludePaging(boolean includePaging) {
		this.includePaging = includePaging;
	}

	@Override
	public int doStartTag() throws JspException {
		String inputs = Util.searchParamsToInputs(pageContext.getRequest().getParameterMap(), includeSorts,
				includeFilters, includePaging);

		try {
			pageContext.getOut().print(inputs);
		} catch (IOException e) {
			throw new JspTagException(e);
		}
		return SKIP_BODY;
	}
}
