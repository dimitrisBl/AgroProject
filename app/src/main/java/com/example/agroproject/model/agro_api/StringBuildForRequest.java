package com.example.agroproject.model.agro_api;

public class StringBuildForRequest {

    // key apo -> 6285cde775f088c749b4f1201829658a
    // key dimitri -> 126813a55ec5e945022783add18142d2

    private static final String AGRO_API_LINK = "http://api.agromonitoring.com/agro/1.0";

    private static final String API_KEY = "6285cde775f088c749b4f1201829658a";


    /**
     * API endpoint for polygons. This url can be used for GET and POST requests.
     *
     * @return a string representing the polygons url of agro api
     */
    public static String polygonsRequestLink(){
        StringBuilder builder = new StringBuilder(AGRO_API_LINK);
        builder.append(String.format("/polygons?appid=%s", API_KEY));
        return builder.toString();
    }

    /**
     * API endpoint for sentinel. This url can be used for
     * Get request for a specific polygon in user choice date.
     *
     * @param polygonId has the polygon Id for which I want to get satellite data
     * @param dateFrom
     * @param dateTo
     * @return a url for endpoint sentinel of agro api in string type.
     */
    public static String sentinelRequestLink(String polygonId, String dateFrom, String dateTo){
        StringBuilder builder = new StringBuilder(AGRO_API_LINK);
        builder.append(String.format("/image/search?start="+dateFrom+"&end="+dateTo+"&polyid="+polygonId+"&&appid=%s", API_KEY));
        return builder.toString();
    }
}
