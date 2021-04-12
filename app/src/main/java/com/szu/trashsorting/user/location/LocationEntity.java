package com.szu.trashsorting.user.location;

public class LocationEntity {
    public String status;
    public Result result;
    public static class Result{
        public MyLocation location;
        public String formatted_address;
        public String business;
        public Address addressComponent;
        public String cityCode;
        public static class MyLocation{
            public String lng;
            public String lat;
        }
        public static class Address{
            public String city;
            public String direction;
            public String distance;
            public String district;
            public String province;
            public String street;
            public String street_number;
        }
    }

}
