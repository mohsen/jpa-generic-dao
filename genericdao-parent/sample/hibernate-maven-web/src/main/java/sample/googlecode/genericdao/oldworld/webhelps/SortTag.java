package sample.googlecode.genericdao.oldworld.webhelps;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Link to be used for sorting in a table header. It checks the request
 * parameters to see if a sort is currently active on the given path. If so it
 * applies either the sort_asc or sort_desc class to the link. Clicking the link
 * will reload the page with the given path specified as the sort parameter. Any
 * other search parameters (i.e. filters, paging) are preserved by this link.
 * 
 * @author dwolverton
 * 
 */
public class SortTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	@Override
	public void setPageContext(PageContext pageContext) {
		styleClass = null;
		attributes = new StringBuilder();
		super.setPageContext(pageContext);
	}

	String path;
	String styleClass;

	public void setTitle(String value) {
		addAttribute("title", value);
	}

	public void setStyleClass(String value) {
		styleClass = value;
	}

	public void setStyle(String value) {
		addAttribute("style", value);
	}

	public void setPath(String path) {
		this.path = path;
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
		String href = "?sort=" + path;
		String klass = null;

		String[] sorts = pageContext.getRequest().getParameterValues("sort");
		if (sorts != null) {
			for (String sort : sorts) {
				if (path.equals(sort)) {
					href = "?sort=!" + path;
					klass = "sort_asc";
					break;
				} else if (("!" + path).equals(sort)) {
					klass = "sort_desc";
					break;
				}
			}
		}

		if (klass != null) {
			if (styleClass == null || "".equals(styleClass))
				styleClass = klass;
			else
				styleClass += " " + klass;
		}

		String searchParams = Util.searchParamsToURL(pageContext.getRequest().getParameterMap(), false, true, true);
		if (searchParams != null && !searchParams.equals("")) {
			href += "&" + searchParams;
		}

		try {
			pageContext.getOut().print("<a href=\"");
			pageContext.getOut().print(href);
			pageContext.getOut().print("\"");
			if (styleClass != null && !styleClass.equals("")) {
				pageContext.getOut().print(" class=\"");
				pageContext.getOut().print(styleClass);
				pageContext.getOut().print("\"");
			}
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
			pageContext.getOut().print("</a>");
		} catch (IOException e) {
			throw new JspTagException(e);
		}
		return EVAL_PAGE;
	}
}
