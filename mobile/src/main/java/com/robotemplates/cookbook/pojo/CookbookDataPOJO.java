package com.robotemplates.cookbook.pojo;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "success",
        "categories",
        "ingredients",
        "recipes"
})
public class CookbookDataPOJO {

    @JsonProperty("success")
    private Boolean success;
    @JsonProperty("categories")
    private List<Category> categories = null;
    @JsonProperty("ingredients")
    private List<Ingredient> ingredients = null;
    @JsonProperty("recipes")
    private List<Recipe> recipes = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("success")
    public Boolean getSuccess() {
        return success;
    }

    @JsonProperty("success")
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    @JsonProperty("categories")
    public List<Category> getCategories() {
        return categories;
    }

    @JsonProperty("categories")
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    @JsonProperty("ingredients")
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    @JsonProperty("ingredients")
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @JsonProperty("recipes")
    public List<Recipe> getRecipes() {
        return recipes;
    }

    @JsonProperty("recipes")
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "name",
            "image"
    })
    public class Category {

        @JsonProperty("name")
        private String name;
        @JsonProperty("image")
        private String image;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("image")
        public String getImage() {
            return image;
        }

        @JsonProperty("image")
        public void setImage(String image) {
            this.image = image;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "recipe_id",
            "name",
            "quantity",
            "unit"
    })
    public class Ingredient {

        @JsonProperty("recipe_id")
        private String recipe_id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("quantity")
        private String quantity;
        @JsonProperty("unit")
        private String unit;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("recipe_id")
        public String getRecipeId() {
            return recipe_id;
        }

        @JsonProperty("recipe_id")
        public void setRecipeId(String recipe_id) {
            this.recipe_id = recipe_id;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("quantity")
        public String getQuantity() {
            return quantity;
        }

        @JsonProperty("quantity")
        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        @JsonProperty("unit")
        public String getUnit() {
            return unit;
        }

        @JsonProperty("unit")
        public void setUnit(String unit) {
            this.unit = unit;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonPropertyOrder({
            "id",
            "category_id",
            "name",
            "intro",
            "instruction",
            "image",
            "link",
            "time",
            "servings",
            "calories",
            "favorite",
            "viewers_count",
            "favourite_count"
    })
    public class Recipe {

        @JsonProperty("id")
        private String id;
        @JsonProperty("category_id")
        private String category_id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("intro")
        private String intro;
        @JsonProperty("instruction")
        private String instruction;
        @JsonProperty("image")
        private String image;
        @JsonProperty("link")
        private String link;
        @JsonProperty("time")
        private String time;
        @JsonProperty("servings")
        private String servings;
        @JsonProperty("calories")
        private String calories;
        @JsonProperty("favorite")
        private String favorite;
        @JsonProperty("viewers_count")
        private String viewers_count;
        @JsonProperty("favourite_count")
        private String favourite_count;
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        @JsonProperty("id")
        public String getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("category_id")
        public String getCategoryId() {
            return category_id;
        }

        @JsonProperty("category_id")
        public void setCategoryId(String category_id) {
            this.category_id = category_id;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("intro")
        public String getIntro() {
            return intro;
        }

        @JsonProperty("intro")
        public void setIntro(String intro) {
            this.intro = intro;
        }

        @JsonProperty("instruction")
        public String getInstruction() {
            return instruction;
        }

        @JsonProperty("instruction")
        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        @JsonProperty("image")
        public String getImage() {
            return image;
        }

        @JsonProperty("image")
        public void setImage(String image) {
            this.image = image;
        }

        @JsonProperty("link")
        public String getLink() {
            return link;
        }

        @JsonProperty("link")
        public void setLink(String link) {
            this.link = link;
        }

        @JsonProperty("time")
        public String getTime() {
            return time;
        }

        @JsonProperty("time")
        public void setTime(String time) {
            this.time = time;
        }

        @JsonProperty("servings")
        public String getServings() {
            return servings;
        }

        @JsonProperty("servings")
        public void setServings(String servings) {
            this.servings = servings;
        }

        @JsonProperty("calories")
        public String getCalories() {
            return calories;
        }

        @JsonProperty("calories")
        public void setCalories(String calories) {
            this.calories = calories;
        }

        @JsonProperty("favorite")
        public String getFavorite() {
            return favorite;
        }

        @JsonProperty("favorite")
        public void setFavorite(String favorite) {
            this.favorite = favorite;
        }

        @JsonProperty("favourite_count")
        public String getFavoriteCount() {
            return favourite_count;
        }

        @JsonProperty("favourite_count")
        public void setFavoriteCount(String favourite_count) {
            this.favourite_count = favourite_count;
        }

        @JsonProperty("viewers_count")
        public String getViewerCount() {
            return viewers_count;
        }

        @JsonProperty("viewers_count")
        public void setViewerCount(String viewers_count) {
            this.viewers_count = viewers_count;
        }

        @JsonAnyGetter
        public Map<String, Object> getAdditionalProperties() {
            return this.additionalProperties;
        }

        @JsonAnySetter
        public void setAdditionalProperty(String name, Object value) {
            this.additionalProperties.put(name, value);
        }

    }

}





