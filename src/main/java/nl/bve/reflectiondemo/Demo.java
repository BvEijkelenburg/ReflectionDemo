package nl.bve.reflectiondemo;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.lang.reflect.Method;

public class Demo
{
    private static class Country {
        private String name;
        private int population;

        public String getName() { return this.name; }
        public void setName(String name) { this.name = name;}

        public int getPopulation() { return this.population; }
        public void setPopulation(int pop) { this.population = pop;}

        public String toString() {
            return "Country: name=" +name+ ", population=" + population;
        }
    }

    public static void main(String[] args) throws Exception {
        Country country = new Country();

        String jsonBody = "{ \"name\" : \"Netherlands\", \"population\" : 17000000 }";
        JsonReader reader = Json.createReader(new StringReader(jsonBody));

        JsonObject countryJsonObj = reader.readObject();
        System.out.println(country);

        for (String key : countryJsonObj.keySet()) {
            String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);

            Method toCall = null;
            for (Method m : country.getClass().getDeclaredMethods()) {
                if (m.getName().equals(methodName)) {
                    toCall = m;
                    break;
                }
            }

            if (toCall == null || toCall.getParameterTypes().length != 1)
                throw new IllegalStateException("Setter not found, or has less/more than one argument for %s".formatted(methodName));

            Class<?> clazz = toCall.getParameterTypes()[0];

            if (clazz == int.class) {
                toCall.invoke(country, countryJsonObj.getJsonNumber(key).intValue());
            }
            if (clazz == double.class) {
                toCall.invoke(country, countryJsonObj.getJsonNumber(key).doubleValue());
            }
            if (clazz == String.class) {
                toCall.invoke(country, countryJsonObj.getString(key));
            }

        }
        System.out.println(country);
    }
}
