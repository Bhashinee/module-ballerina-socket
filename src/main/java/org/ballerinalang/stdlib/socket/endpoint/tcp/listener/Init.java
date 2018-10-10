/*
 * Copyright (c) 2018 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.stdlib.socket.endpoint.tcp.listener;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Struct;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.stdlib.socket.SocketConstants;
import org.ballerinalang.util.exceptions.BallerinaException;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;

import static org.ballerinalang.stdlib.socket.SocketConstants.SOCKET_PACKAGE;

/**
 * Initialize endpoints.
 */
@BallerinaFunction(
        orgName = "ballerina",
        packageName = "socket",
        functionName = "init",
        receiver = @Receiver(type = TypeKind.OBJECT, structType = "Listener", structPackage = SOCKET_PACKAGE),
        args = {@Argument(name = "config", type = TypeKind.RECORD, structType = "ListenerEndpointConfiguration",
                          structPackage = SOCKET_PACKAGE)
        },
        isPublic = true
)
public class Init extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        try {
            Struct serviceEndpoint = BLangConnectorSPIUtil.getConnectorEndpointStruct(context);
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            serverSocket.socket().setReuseAddress(true);
            serviceEndpoint.addNativeData(SocketConstants.SERVER_SOCKET_KEY, serverSocket);
            BMap<String, BValue> endpointConfig = (BMap<String, BValue>) context.getRefArgument(1);
            serviceEndpoint.addNativeData(SocketConstants.LISTENER_CONFIG, endpointConfig);
        } catch (SocketException e) {
            throw new BallerinaException("Unable to reuse the socket port.");
        } catch (IOException e) {
            throw new BallerinaException(e);
        }
        context.setReturnValues();
    }
}
