package com.robotemplates.cookbook.database.model;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@DatabaseTable(tableName = "recipes")
public class RecipeModel implements Serializable, Comparable<RecipeModel> {
    private static final long serialVersionUID = -222864131214757024L;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CATEGORY_ID = "cat_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_INTRO = "intro";
    public static final String COLUMN_INSTRUCTION = "instruction";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_SERVINGS = "servings";
    public static final String COLUMN_CALORIES = "calories";
    public static final String COLUMN_FAVORITE = "favorite";
    public static final String COLUMN_VIEWER = "viewer";
    public static final String COLUMN_FAVORITE_COUNT = "favorite_count";
    public static final String COLUMN_VIEWER_COUNT = "viewers_count";

    @DatabaseField(columnName = COLUMN_ID, generatedId = true)
    private long id;
    @DatabaseField(foreign = true, index = true)
    private CategoryModel category;
    @DatabaseField(columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(columnName = COLUMN_INTRO)
    private String intro;
    @DatabaseField(columnName = COLUMN_INSTRUCTION)
    private String instruction;
    @DatabaseField(columnName = COLUMN_IMAGE)
    private String image;
    @DatabaseField(columnName = COLUMN_LINK)
    private String link;
    @DatabaseField(columnName = COLUMN_TIME)
    private int time;
    @DatabaseField(columnName = COLUMN_SERVINGS)
    private int servings;
    @DatabaseField(columnName = COLUMN_CALORIES)
    private int calories;
    @DatabaseField(columnName = COLUMN_FAVORITE)
    private boolean favorite;
    @DatabaseField(columnName = COLUMN_VIEWER)
    private boolean viewer;
    @DatabaseField(columnName = COLUMN_CATEGORY_ID)
    private long category_id;
    @DatabaseField(columnName = COLUMN_FAVORITE_COUNT)
    private long favorite_count;
    @DatabaseField(columnName = COLUMN_VIEWER_COUNT)
    private long viewers_count;
    @ForeignCollectionField
    private ForeignCollection<IngredientModel> ingredients; // one to many

    // empty constructor
    public RecipeModel() {
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public CategoryModel getCategory() {
        return category;
    }


    public void setCategory(CategoryModel category) {
        this.category = category;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getIntro() {
        return intro;
    }


    public void setIntro(String intro) {
        this.intro = intro;
    }


    public String getInstruction() {
        return instruction;
    }


    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }


    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }


    public String getLink() {
        return link;
    }


    public void setLink(String link) {
        this.link = link;
    }


    public int getTime() {
        return time;
    }


    public void setTime(int time) {
        this.time = time;
    }


    public int getServings() {
        return servings;
    }


    public void setServings(int servings) {
        this.servings = servings;
    }


    public int getCalories() {
        return calories;
    }


    public void setCalories(int calories) {
        this.calories = calories;
    }


    public boolean isFavorite() {
        return favorite;
    }


    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public long getCategory_id() {
        return category_id;
    }

    public void setCategory_id(long category_id) {
        this.category_id = category_id;
    }

    public long getViewers_count() {
        return viewers_count;
    }

    public void setViewers_count(long viewers_count) {
        this.viewers_count = viewers_count;
    }

    public boolean isViewer() {
        return viewer;
    }

    public void setViewer(boolean viewer) {
        this.viewer = viewer;
    }

    public long getFavorite_count() {
        return favorite_count;
    }

    public void setFavorite_count(long favorite_count) {
        this.favorite_count = favorite_count;
    }

    public List<IngredientModel> getIngredients() {
        List<IngredientModel> list = new ArrayList<>();
        for (IngredientModel m : ingredients) {
            list.add(m);
        }
        return list;
    }


    public void setIngredients(ForeignCollection<IngredientModel> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int compareTo(RecipeModel another) {
        return Integer.valueOf((int) this.favorite_count).compareTo((int) another.favorite_count);
    }

    public static final Comparator<RecipeModel> DESCENDING_COMPARATOR = new Comparator<RecipeModel>() {
        // Overriding the compare method to sort the age
        public int compare(RecipeModel lhs, RecipeModel rhs) {
            return Integer.valueOf((int) lhs.getFavorite_count()).compareTo((int) rhs.getFavorite_count());
        }
    };

    public static final Comparator<RecipeModel> ASCENDING_COMPARATOR = new Comparator<RecipeModel>() {
        // Overriding the compare method to sort the age
        public int compare(RecipeModel lhs, RecipeModel rhs) {
            return Integer.valueOf((int) rhs.getFavorite_count()).compareTo((int) lhs.getFavorite_count());
        }
    };

    public static final Comparator<RecipeModel> DESCENDING_COMPARATOR_VIEW = new Comparator<RecipeModel>() {
        // Overriding the compare method to sort the age
        public int compare(RecipeModel lhs, RecipeModel rhs) {
            return Integer.valueOf((int) lhs.getViewers_count()).compareTo((int) rhs.getViewers_count());
        }
    };

    public static final Comparator<RecipeModel> ASCENDING_COMPARATOR_VIEW = new Comparator<RecipeModel>() {
        // Overriding the compare method to sort the age
        public int compare(RecipeModel lhs, RecipeModel rhs) {
            return Integer.valueOf((int) rhs.getViewers_count()).compareTo((int) lhs.getViewers_count());
        }
    };
}
