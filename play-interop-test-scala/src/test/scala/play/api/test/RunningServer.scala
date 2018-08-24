/*
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package play.api.test

import play.api.Application

/**
 * Contains information about a running TestServer. This object can be
 * used by tests to find out about the running server, e.g. port information.
 *
 * We use a separate class to avoid including mutable state, such as methods
 * for closing the server.
 */
final case class RunningServer(app: Application, endpoints: ServerEndpoints)
