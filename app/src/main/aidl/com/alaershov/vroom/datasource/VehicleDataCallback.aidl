// VehicleDataCallback.aidl
package com.alaershov.vroom.datasource;

interface VehicleDataCallback {

    void onDataChanged(float speed, float rpm);
}
