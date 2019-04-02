/**
 * This file is part of feaggle-server.
 *
 * feaggle-server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * feaggle-server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with feaggle-server.  If not, see <https://www.gnu.org/licenses/>.
 **/
package io.feaggle.server.library.infrastructure.journal

import io.vlingo.symbio.store.common.jdbc.Configuration
import java.sql.Connection

class NoopConfigurationInterest: Configuration.ConfigurationInterest {
    override fun afterConnect(connection: Connection?) {

    }

    override fun beforeConnect(configuration: Configuration?) {
    }

    override fun dropDatabase(connection: Connection?, databaseName: String?) {
    }

    override fun createDatabase(connection: Connection?, databaseName: String?) {
    }
}