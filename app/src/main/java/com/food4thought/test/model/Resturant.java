package com.food4thought.test.model;

import java.util.List;

/**
 * Created by samwdp on 05/04/2016.
 */
public class Resturant {

    private String icon;
    private String id;
    private String name;
    private String place_id;
    private double rating;
    private String reference;
    private String scope;
    private List<String> types;
    private String vacinity;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVacinity() {
        return vacinity;
    }

    public void setVacinity(String vacinity) {
        this.vacinity = vacinity;
    }

    public static class Geometry{
        public static class Location{
            private double lat;
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
        private boolean open_now;
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
        private int height;
        private int width;
        private String photo_reference;
        private List<String> html_attribute;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getPhoto_reference() {
            return photo_reference;
        }

        public void setPhoto_reference(String photo_reference) {
            this.photo_reference = photo_reference;
        }

        public List<String> getHtml_attribute() {
            return html_attribute;
        }

        public void setHtml_attribute(List<String> html_attribute) {
            this.html_attribute = html_attribute;
        }
    }

}
