package com.food4thought.test.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * Created by samwdp on 05/04/2016.
 */
public class RestaurantModel {

    @SerializedName("types")
    private List<String> types;
    @SerializedName("id")
    private String id;
    @SerializedName("place_id")
    private String place_id;
    @SerializedName("icon")
    private String icon;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("scope")
    private String scope;
    @SerializedName("name")
    private String name;
    @SerializedName("rating")
    private float rating;
    @SerializedName("reference")
    private String reference;
    @SerializedName("opening_hours")
    private OpeningHours openingHours;
    @SerializedName("photos")
    private List<Photos> photos;
    @SerializedName("geometry")
    private Geometry geometry;

    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }


    public static class Geometry{

        private Location location;

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public static class Location{
            @SerializedName("lat")
            private double lat;
            @SerializedName("lng")
            private double lng;

            public double getLat() {
                return lat;
            }

            public void setLat(double lat) {
                this.lat = lat;
            }

            public double getLng() {
                return lng;
            }

            public void setLng(double lng) {
                this.lng = lng;
            }
        }
    }

    public static class OpeningHours{
        @SerializedName("open_now")
        private boolean open_now;
        @SerializedName("weekday_text")
        private List<String> weekday_text;

        public boolean isOpen_now() {
            return open_now;
        }

        public void setOpen_now(boolean open_now) {
            this.open_now = open_now;
        }

        public List<String> getWeekday_text() {
            return weekday_text;
        }

        public void setWeekday_text(List<String> weekday_text) {
            this.weekday_text = weekday_text;
        }
    }

    public static class Photos{
        private List<HtmlAttributes> htmlAttributes;
        @SerializedName("photo_reference")
        private String photoReference;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public List<HtmlAttributes> getHtmlAttributes() {
            return htmlAttributes;
        }

        public void setHtmlAttributes(List<HtmlAttributes> htmlAttributes) {
            this.htmlAttributes = htmlAttributes;
        }

        public String getPhotoReference() {
            return photoReference;
        }

        public void setPhotoReference(String photoReference) {
            this.photoReference = photoReference;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public static class HtmlAttributes{
            private String name;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
