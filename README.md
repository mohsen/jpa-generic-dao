# jpa-generic-dao

JPA/Hibernate Generic DAO

JPA Genric DAO is a clone of Hibernate Generic DAO project originally written by David Wolverton:
<https://code.google.com/p/hibernate-generic-dao/>

This project is apparently abandoned, since Google code is down and there is no activity in more than two years.

I use Generic DAO in most of my projects, so decided to share bug fixes to it here. There is still **no** such simple-and-beauty library,
neither [JPA Criteria API](https://docs.oracle.com/javaee/6/tutorial/doc/gjitv.html),
nor [spring-data](http://projects.spring.io/spring-data/) can be compared to the small size and flexibility of this project.
specially one with really small lines of code, perfectly matching a Spring Framework project.


## Why might you consider looking into this framework?

**Generic DAO**: With the sweetness of Java generics, the concept of generic DAOs is not new, and it’s not difficult.
However, we’ve put a lot of work into making these easy to use and robust. So if you like the way we’ve done it, then this
framework provides ready-made code for you. On the other hand if you’d rather make your own, then simply feel free to look 
at our source code for ideas and help on a few potentially tricky issues.

**Search**: Search is the most original and sophisticated part of this framework, and it can be used with or without the 
generic DAO portion of the framework. The search capability is designed around the use-case of a list page with sorting, 
filtering, column selection and paging. However, its use is certainly not limited to that. The value that the search adds 
is simpler, more robust querying with less coding and less testing. It is similar to Hibernate Criteria, but it is simpler 
to use and can easily move across layers of an application including view and even remote layers. Plus is works with both Hibernate and JPA.

**Remote DAO** (for R.I.A.s?): If you you’re like us, you don’t want to write and configure an individual DAO style remote 
service for each entity in a R.I.A. or other client-heavy application. This framework may have the solution. 
We provide utilities and sample code to adapt our single general DAO to any remoting technology interface. 
Just configure this single remote access point and the client can do any basic CRUD or search operation on any entity. 
Again, if you don’t like our way of doing things, maybe you can at least get some ideas from our source code.

**Remote Search** (for R.I.A.s?): As mentioned above, the framework can provide a single point for client-server CRUD and search operations. The framework’s search is meant to be able to cross the client-server boundary. So lists and searches in the client application can take advantage of the same easy-to-use features and consistency that the search functionality provides in the server tier or single tier application. This consistency allowed us to create a reusable collection type in Adobe Flex 3 that is associated with a single search object and automatically updates itself from the server according to the search parameters.

# Maven Installation

TBD

# Spring Integration

TBD

# Hibernate Integration

TBD

# Usage Examples

Here are some examples

    Search search = new Search(Project.class);
    search.addFilterEqual("name", "hibernate-generic-dao");
    search.addFilterLessThan("completionDate", new Date());
    search.addFilterOr( Filter.equal("name", "Jack"), Filter.and( Filter.equal("name", "Jill"), Filter.like("location", "%Chicago%"), Filter.greaterThan("age", 5) ) );
    search.addFilterIn("name", "Jack", "Jill", "Bob");
    search.addFilterNot(Filter.in("name","Jack", "Jill", "Bob"));
    search.addSort("name"); search.addSort("age", true); //descending
    search.setMaxResults(15);
    search.setPage(3);

Nested properties are also fully supported

    search.addFilterEqual("status.name", "active");
    search.addFilterGreaterThan("workgroup.manager.salary", 75000.00);
    search.addSort("status.name"); ```

Calling a search:

    Search search = new Search();
    search.addFilterGreaterThan("userCount", 500);
    search.setMaxResults(15);

get one page of results

    List results = projectDAO.search(search);

get the total number of results (ignores paging)

    int totalResults = projectDAO.count(search);

get one page of results and the total number of results without paging

    SearchResult result = projectDAO.searchAndCount(search);
    results = result.getResults();
    totalResults = results.getTotalCount();

get the average userCount for project matching the filter criteria

    search.addField("userCount", Field.OP_AVG);
    long avgCount = (long) projectDAO.searchUnique(search);

