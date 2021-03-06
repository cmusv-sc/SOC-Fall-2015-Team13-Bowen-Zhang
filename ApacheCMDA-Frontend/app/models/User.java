/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import play.data.validation.Constraints;
import util.APICall;
import util.Constants;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String userName;
    @Constraints.Required
    private String password;
    @Constraints.Required
    private String firstName;
    @Constraints.Required
    private String lastName;
    private String middleInitial;
    private String affiliation;
    private String title;
    @Constraints.Required
    private String email;
    private String mailingAddress;
    private String phoneNumber;
    private String faxNumber;
    private String researchFields;
    private String highestDegree;

    private String url;

    private String token;

    private static final String ADD_USER_CALL = Constants.NEW_BACKEND + "users/add";
    private static final String LOGIN_USER_CALL = Constants.NEW_BACKEND + "users/login";
    private static final String UPDATE_USER_CALL = Constants.NEW_BACKEND + "users/update/";
    private static final String GET_USER_CALL = Constants.NEW_BACKEND + "users/";

    private static final String GET_FOLLOWERS_CALL = Constants.NEW_BACKEND + "users/getfollowers/";
    private static final String GET_PEOPLE_YOU_MAY_FOLLOW_CALL = Constants.NEW_BACKEND + "users/getPeopleYouMayFollow/";
    private static final String FOLLOW_CALL = Constants.NEW_BACKEND + "users/follow";
    private static final String UNFOLLOW_CALL = Constants.NEW_BACKEND + "users/unfollow";
    private static final String DEFAULT_SEARCH_CALL = Constants.NEW_BACKEND + "search/user/";
    private static final String EXACTLY_SEARCH_CALL = Constants.NEW_BACKEND + "searchExactly/user/";
    private static final String FUZZY_SEARCH_CALL = Constants.NEW_BACKEND + "searchFuzzy/user/";


    // @OneToMany(mappedBy = "user", cascade={CascadeType.ALL})
    // private Set<ClimateService> climateServices = new
    // HashSet<ClimateService>();

    public User() {
    }

    public User(String userName, String password, String firstName,
                String lastName, String middleInitial, String affiliation,
                String title, String email, String mailingAddress,
                String phoneNumber, String faxNumber, String researchFields,
                String highestDegree) {
        super();
        this.userName = userName;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleInitial = middleInitial;
        this.affiliation = affiliation;
        this.title = title;
        this.email = email;
        this.mailingAddress = mailingAddress;
        this.phoneNumber = phoneNumber;
        this.faxNumber = faxNumber;
        this.researchFields = researchFields;
        this.highestDegree = highestDegree;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public String getResearchFields() {
        return researchFields;
    }

    public String getHighestDegree() {
        return highestDegree;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = Integer.parseInt(id);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public void setResearchFields(String researchFields) {
        this.researchFields = researchFields;
    }

    public void setHighestDegree(String highestDegree) {
        this.highestDegree = highestDegree;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    /**
     * Create a new user
     *
     * @param jsonData
     * @return the response from the API server
     */
    public static JsonNode create(JsonNode jsonData) {
        return APICall.postAPI(ADD_USER_CALL, jsonData);
    }

    /**
     * update a new user
     *
     * @param jsonData
     * @return the response from the API server
     */
    public static JsonNode update(String id, JsonNode jsonData) {
        return APICall.putAPI(UPDATE_USER_CALL + id, jsonData);
    }

    /**
     * Get a user based on its username
     *
     * @return
     */
    public static User get(String id) {
        JsonNode json = APICall.callAPI(GET_USER_CALL + id);
        User user = new User();
        System.out.println("json is " + json);
        user.setId(json.path("id").asText());
        user.setUserName(json.path("userName").asText());
        user.setFirstName(json.path("firstName").asText());
        user.setAffiliation(json.path("affiliation").asText());
        user.setLastName(json.path("lastName").asText());
        user.setTitle(json.path("title").asText());
        user.setEmail(json.path("email").asText());
        user.setMailingAddress(json.path("mailingAddress").asText());
        user.setPhoneNumber(json.path("phoneNumber").asText());
        user.setResearchFields(json.path("researchFields").asText());
        user.setUrl(json.path("url").asText());
        return user;
    }

    public static void follow(String source, String target) {
        APICall.callAPIParameters(FOLLOW_CALL, "source", source, "target", target);
        System.out.println(FOLLOW_CALL + "?source=" + source + "&target=" + target);

    }

    public static void unfollow(String source, String target) {
        APICall.callAPIParameters(UNFOLLOW_CALL, "source", source, "target", target);
    }


    public static List<User> getFollowers(String id) {
        JsonNode json = APICall
                .callAPI(GET_FOLLOWERS_CALL + id);

        List<User> users = new ArrayList<User>();
        Iterator<JsonNode> iterator = json.elements();
        while (iterator.hasNext()) {
            User user = new User();
            JsonNode token = iterator.next();
            user.setId(token.path("id").asText());
            user.setUserName(token.path("userName").asText());
            user.setFirstName(token.path("firstName").asText());
            user.setAffiliation(token.path("affiliation").asText());
            user.setLastName(token.path("lastName").asText());
            users.add(user);
        }
        System.out.println("json is " + json);
        return users;
    }


    public static List<User> getPeopleYouMayFollow(String id) {
        JsonNode json = APICall.callAPI(GET_PEOPLE_YOU_MAY_FOLLOW_CALL + id);
        List<User> peopleYouMayFollow = new ArrayList<User>();

        Iterator<JsonNode> iterator = json.elements();
        while (iterator.hasNext()) {
            User user = new User();
            JsonNode token = iterator.next();
            user.setId(token.path("id").asText());
            user.setFirstName(token.path("firstName").asText());
            user.setLastName(token.path("lastName").asText());
            user.setResearchFields(token.path("researchFields").asText());
            user.setTitle(token.path("title").asText());
            peopleYouMayFollow.add(user);
        }

        return peopleYouMayFollow;
    }
    /**
     * Verify the password and get the user
     * <p>
     * id,
     * affiliation,
     * email,
     * faxNumber,
     * firstName,
     * highestDegree,
     * lastName,
     * mailingAddress
     * middleInitial
     * password
     * phoneNumber
     * researchFields
     * title
     * userName
     *
     * @return
     */
    public static User verifyAndGet(JsonNode jsonData) {
        JsonNode json = APICall.postAPI(LOGIN_USER_CALL, jsonData);
        User user = new User();
        System.out.println("json is " + json);
        try {
            user.setId(json.path("id").asText());
            user.setAffiliation(json.path("affiliation").asText());
            user.setEmail(json.path("email").asText());
            user.setUserName(json.path("userName").asText());
            user.setFirstName(json.path("firstName").asText());
            user.setLastName(json.path("lastName").asText());
            user.setResearchFields(json.path("researchFields").asText());
            user.setTitle(json.path("title").asText());
            user.setPhoneNumber(json.path("phoneNumber").asText());
            user.setUrl(json.path("url").asText());
            user.setToken(json.path("token").asText());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(json.path("error").asText());
        }
        return user;
    }


    public static List<User> search(String keyword) {
        List<User> searchedUsers = new ArrayList<>();
        if (keyword.trim().equals("")) return searchedUsers;
        JsonNode json = APICall.callAPI(DEFAULT_SEARCH_CALL + keyword);
        for (JsonNode node : json) {
            User user = new User();
            user.setId(node.path("id").asLong());
            user.setEmail(node.path("email").asText());
            user.setFirstName(node.path("firstName").asText());
            user.setLastName(node.path("lastName").asText());
            user.setUrl(node.path("url").asText());
            user.setResearchFields(node.path("researchFields").asText());
            searchedUsers.add(user);
        }
        return searchedUsers;
    }

    public static List<User> search(String keyword, String field, boolean isFuzzy) {
        List<User> searchedUsers = new ArrayList<>();
        if (keyword.trim().equals("")) return searchedUsers;
        keyword = keyword.replace(" ", "_");
        JsonNode json = APICall.callAPI((isFuzzy ? FUZZY_SEARCH_CALL : EXACTLY_SEARCH_CALL) + field + "/" + keyword);
        System.out.println(isFuzzy ? FUZZY_SEARCH_CALL : EXACTLY_SEARCH_CALL + field + "/" + keyword);
        for (JsonNode node : json) {
            User user = new User();
            user.setId(node.path("id").asLong());
            user.setEmail(node.path("email").asText());
            user.setFirstName(node.path("firstName").asText());
            user.setLastName(node.path("lastName").asText());
            user.setUrl(node.path("url").asText());
            user.setResearchFields(node.path("researchFields").asText());
            searchedUsers.add(user);
        }
        return searchedUsers;
    }


    @Override
    public String toString() {
        return "User [id=" + id + ", userName=" + userName + ", password="
                + password + ", firstName=" + firstName + ", lastName="
                + lastName + ", middleInitial=" + middleInitial
                + ", affiliation=" + affiliation + ", title=" + title
                + ", email=" + email + ", mailingAddress=" + mailingAddress
                + ", phoneNumber=" + phoneNumber + ", faxNumber=" + faxNumber
                + ", researchFields=" + researchFields + ", highestDegree="
                + highestDegree + "]";
    }

    public int hashCode() {
        return (int) this.id;
    }


}

