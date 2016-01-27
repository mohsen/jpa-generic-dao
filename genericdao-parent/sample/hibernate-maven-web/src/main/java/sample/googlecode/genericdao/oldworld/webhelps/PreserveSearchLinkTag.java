package sample.googlecode.genericdao.oldworld.webhelps;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * A tag that creates a link and copies search parameters from the request to
 * URL parameters on the link. Thus the search parameters will again be passed
 * in the next request when the link is followed.
 * 
 * @author dwolverton
 * 
 */
public class PreserveSearchLinkTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	String href;
	boolean includeSorts;
	boolean includeFilters;
	boolean includePaging;
	boolean disabled;

	@Override
	public void setPageContext(PageContext pageContext) {
		includeFilters = true;
		includeSorts = true;
		includePaging = true;
		disabled = false;
		attributes = new StringBuilder();
		super.setPageContext(pageContext);
	}

	public void setHref(String href) {
		this.href = href;
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

	public void setTitle(String value) {
		addAttribute("title", value);
	}

	public void setStyleClass(String value) {
		addAttribute("class", value);
	}

	public void setTarget(String value) {
		addAttribute("target", value);
	}

	public void setStyle(String value) {
		addAttribute("style", value);
	}

	public void setDisabled(boolean value) {
		disabled = value;
	}

	StringBuilder attributes = new StringBuilder();

	private void addAttribute(String name, String value) {
		if (!"".equals(value)) {
			attributes.append(" ");
			attributes.append(name);
			attributes.append("=\"");
			attributes.append(value);
			attributes.append("\"");
		}
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			if (disabled) {
				pageContext.getOut().print("<span class=\"disabledLink\">");
				return EVAL_BODY_INCLUDE;
			}

			href = Util.addSearchParamsToURL(href, pageContext.getRequest().getParameterMap(), includeSorts,
					includeFilters, includePaging);

			pageContext.getOut().print("<a href=\"");
			pageContext.getOut().print(href);
			pageContext.getOut().print("\"");
			pageContext.getOut().print(attributes.toString());
			pageContext.getOut().print("/>");
		} catch (IOException e) {
			throw new JspTagException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		try {
			if (disabled) {
				pageContext.getOut().print("</span>");
			} else {
				pageContext.getOut().print("</a>");
			}
		} catch (IOException e) {
			throw new JspTagException(e);
		}
		return EVAL_PAGE;
	}
}
