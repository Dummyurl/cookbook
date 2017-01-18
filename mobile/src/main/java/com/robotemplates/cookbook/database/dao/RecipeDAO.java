package com.robotemplates.cookbook.database.dao;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.robotemplates.cookbook.database.DatabaseHelper;
import com.robotemplates.cookbook.database.model.RecipeModel;

import java.sql.SQLException;
import java.util.List;


public class RecipeDAO extends DAO {
    public static int create(RecipeModel recipe) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        return dao.create(recipe);
    }


    public static RecipeModel read(long id) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        return dao.queryForId(id);
    }


    public static List<RecipeModel> readAll(long skip, long take) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;
        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }


    public static List<RecipeModel> readFavorites(long skip, long take) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;
        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }


    public static List<RecipeModel> readByCategory(long categoryId, long skip, long take) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;
        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_CATEGORY_ID, categoryId);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_CATEGORY_ID, categoryId);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }

    public static List<RecipeModel> sortByFavourite(long skip, long take) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;
        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            //queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            //queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }

    public static List<RecipeModel> sortByViewers(long skip, long take) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;
        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            //queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            //queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }

    public static List<RecipeModel> sortByCategoryFav(long categoryId, long skip, long take, String query) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;

        if (categoryId == -1L) {
            if (skip == -1L && take == -1L) {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
                list = dao.query(queryBuilder.prepare());
            } else {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
                queryBuilder.offset(skip).limit(take);
                list = dao.query(queryBuilder.prepare());
            }
            return list;
        }

        if (categoryId == -2L) {
            if (skip == -1L && take == -1L) {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true);
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
                list = dao.query(queryBuilder.prepare());
            } else {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true);
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
                queryBuilder.offset(skip).limit(take);
                list = dao.query(queryBuilder.prepare());
            }
            return list;
        }

        if (categoryId == -3L) {
            if (skip == -1L && take == -1L) {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where()
                        .like(RecipeModel.COLUMN_NAME, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INTRO, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INSTRUCTION, "%" + query + "%");
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                list = dao.query(queryBuilder.prepare());
            } else {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where()
                        .like(RecipeModel.COLUMN_NAME, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INTRO, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INSTRUCTION, "%" + query + "%");
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                queryBuilder.offset(skip).limit(take);
                list = dao.query(queryBuilder.prepare());
            }
            return list;
        }

        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_CATEGORY_ID, categoryId);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            //queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_CATEGORY_ID, categoryId);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            //queryBuilder.orderBy(RecipeModel.COLUMN_FAVORITE_COUNT, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }

    public static List<RecipeModel> sortByCategoryViewers(long categoryId, long skip, long take, String query) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;

        if (categoryId == -1L) {
            if (skip == -1L && take == -1L) {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
                list = dao.query(queryBuilder.prepare());
            } else {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
                queryBuilder.offset(skip).limit(take);
                list = dao.query(queryBuilder.prepare());
            }
            return list;
        }

        if (categoryId == -2L) {
            if (skip == -1L && take == -1L) {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true);
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
                list = dao.query(queryBuilder.prepare());
            } else {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq(RecipeModel.COLUMN_FAVORITE, true);
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                //queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
                queryBuilder.offset(skip).limit(take);
                list = dao.query(queryBuilder.prepare());
            }
            return list;
        }

        if (categoryId == -3L) {
            if (skip == -1L && take == -1L) {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where()
                        .like(RecipeModel.COLUMN_NAME, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INTRO, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INSTRUCTION, "%" + query + "%");
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                list = dao.query(queryBuilder.prepare());
            } else {
                QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
                queryBuilder.where()
                        .like(RecipeModel.COLUMN_NAME, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INTRO, "%" + query + "%")
                        .or()
                        .like(RecipeModel.COLUMN_INSTRUCTION, "%" + query + "%");
                queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
                queryBuilder.offset(skip).limit(take);
                list = dao.query(queryBuilder.prepare());
            }
            return list;
        }

        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_CATEGORY_ID, categoryId);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            //queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq(RecipeModel.COLUMN_CATEGORY_ID, categoryId);
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            //queryBuilder.orderBy(RecipeModel.COLUMN_VIEWER_COUNT, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }


    public static int update(RecipeModel recipe) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        return dao.update(recipe);
    }


    public static int delete(long id) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        return dao.deleteById(id);
    }


    public static int deleteAll() throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        DeleteBuilder<RecipeModel, Long> deleteBuilder = dao.deleteBuilder();
        return dao.delete(deleteBuilder.prepare());
    }


    public static List<RecipeModel> search(String query, long skip, long take) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        List<RecipeModel> list;
        if (skip == -1L && take == -1L) {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where()
                    .like(RecipeModel.COLUMN_NAME, "%" + query + "%")
                    .or()
                    .like(RecipeModel.COLUMN_INTRO, "%" + query + "%")
                    .or()
                    .like(RecipeModel.COLUMN_INSTRUCTION, "%" + query + "%");
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            list = dao.query(queryBuilder.prepare());
        } else {
            QueryBuilder<RecipeModel, Long> queryBuilder = dao.queryBuilder();
            queryBuilder.where()
                    .like(RecipeModel.COLUMN_NAME, "%" + query + "%")
                    .or()
                    .like(RecipeModel.COLUMN_INTRO, "%" + query + "%")
                    .or()
                    .like(RecipeModel.COLUMN_INSTRUCTION, "%" + query + "%");
            queryBuilder.orderBy(RecipeModel.COLUMN_NAME, true);
            queryBuilder.offset(skip).limit(take);
            list = dao.query(queryBuilder.prepare());
        }
        return list;
    }


    public static int refresh(RecipeModel recipe) throws SQLException {
        Dao<RecipeModel, Long> dao = getDao();
        return dao.refresh(recipe);
    }


    public static Dao<RecipeModel, Long> getDao() throws SQLException {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
        return databaseHelper.getRecipeDao();
    }
}
