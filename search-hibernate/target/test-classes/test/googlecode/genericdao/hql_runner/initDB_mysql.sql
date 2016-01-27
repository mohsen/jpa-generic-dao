/* This SQL file works with MySQL */


SET NAMES utf8;

SET SQL_MODE='';
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';

/*Data for the table `address` */

insert into `address` (`id`,`city`,`state`,`street`,`zip`) values (1,'Chicago','IL','940 N Fairfield','60610'),(2,'Chicago','IL','734 N Fairfield, Apt 3','60610'),(3,'Chicago','IL','3290 W Fulton','60610');

/*Data for the table `home` */

insert into `home` (`id`,`type`,`address_id`) values (1,'house',1),(2,'apartment',2),(3,'house',3);

/*Data for the table `ingredient` */

insert into `ingredient` (`ingredientId`,`name`) values (1,'Sugar'),(2,'Butter'),(3,'Flour'),(4,'Salt'),(5,'Yeast'),(6,'Chicken');

/*Data for the table `person` */

insert into `person` (`id`,`age`,`dob`,`first_name`,`last_name`,`weight`,`father_id`,`home_id`,`mother_id`) values (1,65,'1944-03-31 11:57:16','Grandpa','Alpha',100.65,NULL,3,NULL),(2,65,'1944-03-31 11:57:16','Grandma','Alpha',100.65,NULL,3,NULL),(3,39,'1970-03-31 11:57:16','Papa','Alpha',100.39,1,1,2),(4,40,'1969-03-31 11:57:16','Mama','Alpha',100.4,NULL,1,NULL),(5,39,'1970-03-31 11:57:16','Papa','Beta',100.39,NULL,2,NULL),(6,38,'1971-03-31 11:57:16','Mama','Beta',100.38,1,2,2),(7,10,'1999-03-31 11:57:16','Joe','Alpha',100.1,3,1,4),(8,9,'2000-03-31 11:57:16','Sally','Alpha',100.09,3,1,4),(9,10,'1999-03-31 11:57:16','Joe','Beta',100.1,5,2,6),(10,14,'1995-03-31 11:57:16','Margaret','Beta',100.14,5,2,6);

/*Data for the table `pet` */

insert into `pet` (`limbed`,`id`,`idNumber`,`first`,`last`,`species`,`hasPaws`,`favoritePlaymate_id`) values (1,1,4444,'Jimmy',NULL,'spider','\0',1),(0,2,1111,'Mr','Wiggles','fish',NULL,1),(1,3,2222,'Miss','Prissy','cat','\0',2),(1,4,3333,'Norman',NULL,'cat','\0',1);

/*Data for the table `pet_limbs` */

insert into `pet_limbs` (`Pet_id`,`element`,`idx`) values (1,'left front leg',0),(1,'right front leg',1),(1,'left frontish leg',2),(1,'right frontish leg',3),(1,'left hindish leg',4),(1,'right hindish leg',5),(1,'left hind leg',6),(1,'right hind leg',7),(3,'left front leg',0),(3,'right front leg',1),(3,'left hind leg',2),(3,'right hind leg',3),(4,'left front leg',0),(4,'right front leg',1),(4,'left hind leg',2),(4,'right hind leg',3);

/*Data for the table `project` */

insert into `project` (`id`,`inceptionYear`,`name`) values (1,1900,'First'),(2,1950,'Second'),(3,2000,'Third');

/*Data for the table `project_person` */

insert into `project_person` (`Project_id`,`members_id`) values (1,7),(1,9),(2,7),(2,8),(3,3),(3,4),(3,5),(3,6);

/*Data for the table `recipe` */

insert into `recipe` (`id`,`title`) values (1,'Bread'),(2,'Fried Chicken'),(3,'Toffee');

/*Data for the table `recipe_x_ingredient` */

insert into `recipe_x_ingredient` (`amount`,`measure`,`recipe_id`,`ingredient_ingredientId`) values (2,'cups',3,1),(0.25,'cup',2,2),(2,'cups',3,2),(4,'cups',1,3),(1,'cup',2,3),(0.5,'tsp.',1,4),(0.5,'tsp.',2,4),(1,'Tbs.',1,5),(6,'pieces',2,6);

/*Data for the table `store` */

insert into `store` (`id`,`name`) values (1,'Billy\'s Mini-Mart'),(2,'Tom\'s Convenience Store');

/*Data for the table `store_ingredient` */

insert into `store_ingredient` (`Store_id`,`ingredientsCarried_ingredientId`) values (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(2,1),(2,2),(2,4);

SET SQL_MODE=@OLD_SQL_MODE;