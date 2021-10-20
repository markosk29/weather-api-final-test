package com.hackerrank.weather;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackerrank.weather.model.WeatherInput;
import com.hackerrank.weather.output.WeatherJSON;
import com.hackerrank.weather.repository.WeatherRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class WeatherApiRestControllerTest {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final ObjectMapper om = new ObjectMapper();
    @Autowired
    WeatherRepository weatherRepository;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setup() {
        weatherRepository.deleteAll();
        om.setDateFormat(simpleDateFormat);
    }

    @Test
    public void testWeatherEndpointWithPOST() throws Exception {
        WeatherInput expectedRecord = getTestData().get("chicago");
        WeatherJSON actualRecord = om.readValue(mockMvc.perform(post("/weather")
                .contentType("application/json")
                .content(om.writeValueAsString(getTestData().get("chicago"))))
                .andDo(print())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), WeatherJSON.class);

        Assert.assertTrue(new ReflectionEquals(WeatherJSON.from(expectedRecord), "id").matches(actualRecord));
        assertTrue(weatherRepository.findById(actualRecord.getId()).isPresent());
    }

    @Test
    public void testWeatherEndpointWithGETList() throws Exception {
        Map<String, WeatherInput> data = getTestData();
        data.remove("moscow2");
        List<WeatherInput> expectedRecords = new ArrayList<>();

        for (Map.Entry<String, WeatherInput> kv : data.entrySet()) {
            expectedRecords.add(om.readValue(mockMvc.perform(post("/weather")
                    .contentType("application/json")
                    .content(om.writeValueAsString(kv.getValue())))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), WeatherInput.class));
        }
        expectedRecords.sort(Comparator.comparing(WeatherInput::getId));

        List<WeatherInput> actualRecords = om.readValue(mockMvc.perform(get("/weather"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<WeatherInput>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expectedRecords.get(i)).matches(actualRecords.get(i)));
        }
    }

    @Test
    public void testWeatherEndpointWithGETListAndDateFilter() throws Exception {
        String date = "2019-03-12";
        Map<String, WeatherInput> data = getTestData();
        data.remove("moscow2");
        List<WeatherInput> expectedRecords = new ArrayList<>();

        for (Map.Entry<String, WeatherInput> kv : data.entrySet()) {
            expectedRecords.add(om.readValue(mockMvc.perform(post("/weather")
                    .contentType("application/json")
                    .content(om.writeValueAsString(kv.getValue())))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), WeatherInput.class));
        }
        expectedRecords = expectedRecords.stream().filter(r -> r.getDate().equals(date)).collect(Collectors.toList());

        List<WeatherInput> actualRecords = om.readValue(mockMvc.perform(get("/weather?date=2019-03-12"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<WeatherInput>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(expectedRecords.get(i)).matches(actualRecords.get(i)));
        }

        mockMvc.perform(get("/weather?date=2015-06-06"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    public void testWeatherEndpointWithGETListAndCityFilter() throws Exception {
        List<WeatherInput> originalResponse = new ArrayList<>();

        for (Map.Entry<String, WeatherInput> kv : getTestData().entrySet()) {
            originalResponse.add(om.readValue(mockMvc.perform(post("/weather")
                    .contentType("application/json")
                    .content(om.writeValueAsString(kv.getValue())))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), WeatherInput.class));
        }

        //test single
        List<WeatherInput> expectedRecords = originalResponse.stream().filter(r -> r.getCity().toLowerCase().equals("moscow")).collect(Collectors.toList());
        List<WeatherJSON> actualRecords = om.readValue(mockMvc.perform(get("/weather?city=moscow"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<WeatherJSON>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(WeatherJSON.from(expectedRecords.get(i))).matches(actualRecords.get(i)));
        }

        //test multiple
        expectedRecords = originalResponse.stream().filter(r -> ("moscow,London,ChicaGo").toLowerCase().contains(r.getCity().toLowerCase())).collect(Collectors.toList());

        actualRecords = om.readValue(mockMvc.perform(get("/weather?city=moscow,London,ChicaGo"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<WeatherJSON>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(WeatherJSON.from(expectedRecords.get(i))).matches(actualRecords.get(i)));
        }

        //test none
        mockMvc.perform(get("/weather?city=berlin,amsterdam"))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andExpect(status().isOk());
    }

    @Test
    public void testWeatherEndpointWithGETListAndDateOrder() throws Exception {
        List<WeatherInput> expectedRecords = new ArrayList<>();

        for (Map.Entry<String, WeatherInput> kv : getTestData().entrySet()) {
            expectedRecords.add(om.readValue(mockMvc.perform(post("/weather")
                    .contentType("application/json")
                    .content(om.writeValueAsString(kv.getValue())))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), WeatherInput.class));
        }
        expectedRecords.sort(Comparator.comparing(WeatherInput::getDate).thenComparing(WeatherInput::getId));

        List<WeatherJSON> actualRecords = om.readValue(mockMvc.perform(get("/weather?sort=date"))
                .andDo(print())
                .andExpect(jsonPath("$", isA(ArrayList.class)))
                .andExpect(jsonPath("$", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<WeatherJSON>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(WeatherJSON.from(expectedRecords.get(i))).matches(actualRecords.get(i)));
        }

        expectedRecords.sort(Comparator.comparing(WeatherInput::getDate, Comparator.reverseOrder()).thenComparing(WeatherInput::getId));

        actualRecords = om.readValue(mockMvc.perform(get("/weather?sort=-date"))
                .andDo(print())
                .andExpect(jsonPath("$", isA(ArrayList.class)))
                .andExpect(jsonPath("$", hasSize(expectedRecords.size())))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<WeatherJSON>>() {
        });

        for (int i = 0; i < expectedRecords.size(); i++) {
            Assert.assertTrue(new ReflectionEquals(WeatherJSON.from(expectedRecords.get(i))).matches(actualRecords.get(i)));
        }
    }

    @Test
    public void testWeatherEndpointWithGETById() throws Exception {
        WeatherInput expectedRecord = om.readValue(mockMvc.perform(post("/weather")
                .contentType("application/json")
                .content(om.writeValueAsString(getTestData().get("chicago"))))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), WeatherInput.class);

        WeatherJSON actualRecord = om.readValue(mockMvc.perform(get("/weather/" + expectedRecord.getId())
                .contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), WeatherJSON.class);

        Assert.assertTrue(new ReflectionEquals(WeatherJSON.from(expectedRecord)).matches(actualRecord));

        mockMvc.perform(get("/weather/" + Integer.MAX_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    private Map<String, WeatherInput> getTestData()  {
        Map<String, WeatherInput> data = new LinkedHashMap<>();

        WeatherInput chicago = new WeatherInput(
                "2019-06-11",
                41.8818f,
                -87.6231f,
                "Chicago",
                "Illinois",
                Arrays.asList(24.0, 21.5, 24.0, 19.5, 25.5, 25.5, 24.0, 25.0, 23.0, 22.0, 18.0, 18.0, 23.5, 23.0, 23.0, 25.5, 21.0, 20.5, 20.0, 18.5, 20.5, 21.0, 25.0, 20.5));
        data.put("chicago", chicago);

        WeatherInput oakland = new WeatherInput(
                "2019-06-12",
                37.8043f,
                -122.2711f,
                "Oakland",
                "California",
                Arrays.asList(24.0, 36.0, 28.5, 29.0, 32.0, 36.0, 28.5, 34.5, 30.5, 31.5, 29.5, 27.0, 30.5, 23.5, 29.0, 22.0, 28.5, 32.5, 24.5, 28.5, 22.5, 35.0, 26.5, 32.5));
        data.put("oakland", oakland);

        WeatherInput london = new WeatherInput(
                "2019-03-12",
                51.5098f,
                -0.1180f,
                "London",
                "N/A",
                Arrays.asList(11.0, 11.0, 5.5, 7.0, 5.0, 5.5, 6.0, 9.5, 11.5, 5.0, 6.0, 8.0, 9.5, 5.0, 9.0, 9.5, 12.0, 6.0, 9.5, 8.5, 8.0, 8.0, 9.0, 6.5));
        data.put("london", london);

        WeatherInput moscow1 = new WeatherInput(
                "2019-03-12",
                55.7512f,
                37.6184f,
                "Moscow",
                "N/A",
                Arrays.asList(-2.0, -4.5, 1.0, -6.0, 1.0, 1.5, -9.0, -2.5, -3.0, -0.5, -13.5, -9.0, -11.5, -5.5, -5.5, -3.5, -14.0, -9.5, 1.5, -15.0, -6.5, -7.0, -13.5, -14.5));
        data.put("moscow1", moscow1);

        WeatherInput moscow2 = new WeatherInput(
                "2019-03-12",
                55.7512f,
                37.6184f,
                "Moscow",
                "N/A",
                Arrays.asList(-2.0, -4.5, 1.0, -6.0, 1.0, 1.5, -9.0, -2.5, -3.0, -0.5, -13.5, -9.0, -11.5, -5.5, -5.5, -3.5, -14.0, -9.5, 1.5, -15.0, -6.5, -7.0, -13.5, -14.5));
        data.put("moscow2", moscow2);

        return data;
    }
}
