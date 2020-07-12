// VehicleDataSourceServiceInterface.aidl
package com.alaershov.vroom.datasource;

import com.alaershov.vroom.datasource.VehicleDataCallback;

interface VehicleDataSourceServiceInterface {

    void registerCallback(VehicleDataCallback callback);

    void unregisterCallback(VehicleDataCallback callback);
}
