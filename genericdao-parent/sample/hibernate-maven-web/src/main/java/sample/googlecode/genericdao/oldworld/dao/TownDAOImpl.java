package sample.googlecode.genericdao.oldworld.dao;

import org.springframework.stereotype.Repository;

import sample.googlecode.genericdao.oldworld.model.Town;

/**
 * <p>
 * This is the implementation of the Town DAO. You can see that we don't
 * actually have to implement anything, it is all inherited from GenericDAOImpl
 * through BaseDAO. We just specify the entity type (Town) and its identifier
 * type (Long).
 * 
 * <p>
 * The @Repository allows Spring to recognize this as a managed component (so we
 * don't need to specify it in XML) and also tells spring to do DAO exception
 * translation to the Spring exception hierarchy.
 * 
 * @author dwolverton
 * 
 */
@Repository
public class TownDAOImpl extends BaseDAO<Town, Long> implements TownDAO {

}
