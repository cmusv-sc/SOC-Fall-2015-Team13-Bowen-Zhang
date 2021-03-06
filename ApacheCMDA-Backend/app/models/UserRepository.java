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

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many standard
 * operations here.
 */
@Named
@Singleton
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findByUserName(String userName);

    User findByEmail(String email);

    List<User> findByCluster(int cluster);

    @Query(value = "select u.* from User u where u.id=?", nativeQuery = true)
    User findByID(long id);

    @Query(value = "select User.* from User, Following where ((Following.target = ?1) and (User.id = Following.source))", nativeQuery = true)
    List<User> findFollowers(long user_ID);

    @Query(value = "select User.* from User, Following where ((Following.source = ?1) and (User.id = Following.target))", nativeQuery = true)
    List<User> findFolloweesByUser(long user_ID);

    @Query(value = "select ifnull(max(u.id), 0) from User u  order by u.id desc limit 1", nativeQuery = true)
    long latestID();
}
