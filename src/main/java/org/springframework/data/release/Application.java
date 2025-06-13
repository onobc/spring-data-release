/*
 * Copyright 2015-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.release;

import java.security.Security;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.shell.core.ExitShellRequest;
import org.springframework.shell.support.logging.HandlerUtils;

/**
 * @author Oliver Gierke
 */
@SpringBootApplication
public class Application {

	public static void main(String[] args) throws Exception {

		// without this, you'll get weird errors like "Cannot find any provider supporting AES/OCB/NoPadding"
		Security.addProvider(new BouncyCastleProvider());

		SpringApplication application = new SpringApplication(Application.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.setAdditionalProfiles("local");
		ExitShellRequest exitShellRequest = null;

		try (ConfigurableApplicationContext context = application.run(args)) {
			BootShim bs = new BootShim(args, context);
			exitShellRequest = bs.run();

		} catch (RuntimeException e) {
			throw e;
		} finally {
			HandlerUtils.flushAllHandlers(Logger.getLogger(""));
		}

		if (exitShellRequest != null) {
			System.exit(exitShellRequest.getExitCode());
		}
	}
}
