package sample.googlecode.genericdao.oldworld.dao;

import sample.googlecode.genericdao.oldworld.model.Town;

import com.googlecode.genericdao.dao.hibernate.GenericDAO;


/**
 * <p>
 * Interface for the Town DAO. This is created very simply by extending
 * GenericDAO and specifying the type for the entity class (Town) and the
 * type of its identifier (Long).
 * 
 * <p>
 * As a matter of best practice other components reference this interface rather
 * than the implementation of the DAO itself.
 * 
 * @author dwolverton
 * 
 */
public interface TownDAO extends GenericDAO<Town, Long> {

}
