package com.example.sponsorships.enums;

/**
 * Enum koji predstavlja gradove s njihovim nazivima i poštanskim brojevima.
 */
public enum CITY {

    ZAGREB("Zagreb", 10000),
    SPLIT("Split", 21000),
    RIJEKA("Rijeka", 51000),
    OSIJEK("Osijek", 31000),
    VARAZDIN("Varaždin", 42000),
    KRAPINA("Krapina", 49000),
    ZADAR("Zadar", 23000),
    DUBROVNIK("Dubrovnik", 20000)
    ;

    private final String cityName;
    private final Integer postalCode;

    /**
     * Konstruktor za definiranje grada s imenom i poštanskim brojem.
     *
     * @param cityName naziv grada
     * @param postalCode poštanski broj grada
     */
    CITY(String cityName, Integer postalCode) {
        this.cityName = cityName;
        this.postalCode = postalCode;
    }

    /**
     * Dohvaća naziv grada.
     *
     * @return naziv grada
     */
    public String getCityName() {
        return cityName;
    }

    /**
     * Dohvaća poštanski broj grada.
     *
     * @return poštanski broj grada
     */
    public Integer getPostalCode() {
        return postalCode;
    }

    /**
     * Metoda koja vraća enum vrijednost grada prema nazivu grada.
     * Ako naziv ne postoji, vraća {@code ZAGREB} kao default.
     *
     * @param name naziv grada
     * @return enum vrijednost grada
     */
    public static CITY getCityByName(String name){
        for(CITY city : CITY.values()){
            if(city.cityName.equalsIgnoreCase(name)){
                return city;
            }
        }
        return ZAGREB;
    }
}
