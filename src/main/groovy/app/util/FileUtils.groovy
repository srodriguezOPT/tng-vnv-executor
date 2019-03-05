/*
 * Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 * ALL RIGHTS RESERVED.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 * nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * This work has been performed in the framework of the SONATA project,
 * funded by the European Commission under Grant number 671517 through
 * the Horizon 2020 and 5G-PPP programmes. The authors would like to
 * acknowledge the contributions of their colleagues of the SONATA
 * partner consortium (www.sonata-nfv.eu).
 *
 * This work has been performed in the framework of the 5GTANGO project,
 * funded by the European Commission under Grant number 761493 through
 * the Horizon 2020 and 5G-PPP programmes. The authors would like to
 * acknowledge the contributions of their colleagues of the 5GTANGO
 * partner consortium (www.5gtango.eu).
 */

package app.util


import app.model.docker_compose.Service
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import java.nio.file.FileAlreadyExistsException

@Component
@PropertySource("classpath:application.properties")
@Scope(value = "singleton")

class FileUtils {

    @Value('${DC.TEST_PATH}')
    private String TEST_PATH

    @Value('${DC.RESULTS_PATH}')
    private String RESULTS_PATH

    private static FileUtils instance = null

    private FileUtils() {}

    static FileUtils getInstance() {
        if(!instance) {
            instance = new FileUtils()
        }
        return instance
    }

    void createTestDirectories(String testId, List<Service> services) throws FileAlreadyExistsException, RuntimeException{

        def rootDir = new File(String.format(TEST_PATH, testId))
        if(rootDir.exists()) {
            throw new FileAlreadyExistsException("Directory ${rootDir.getAbsolutePath()} already exists")
        }

        if (!rootDir.mkdir()) {
            throw new RuntimeException("Error creating the test directory: ${rootDir.getAbsolutePath()}")
        }

        for(service in services) {
            def resultsDir = new File(String.format(RESULTS_PATH, testId, service.name))
            if(resultsDir.exists()) {
                throw new FileAlreadyExistsException("Directory ${resultsDir.getAbsolutePath()} already exists")
            }

            if (!resultsDir.mkdirs()) {
                throw new RuntimeException("Error creating the test result directory: ${resultsDir.getAbsolutePath()}")
            }
        }
    }

    void createDockerComposeFile(String testId, String dockerCompose) throws FileNotFoundException {

        def path = String.format(TEST_PATH, testId)
        def rootDir = new File(path)
        if(!rootDir.exists()) {
            throw new FileNotFoundException("Directory ${rootDir.getAbsolutePath()} not found")
        }

        new File("${path}/docker-compose.yml").withWriter('utf-8') { writer ->
            writer.write(dockerCompose)
        }
    }
}
