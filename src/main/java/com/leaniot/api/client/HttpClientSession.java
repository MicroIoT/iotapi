package com.leaniot.api.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.leaniot.api.HttpSession;
import com.leaniot.api.dto.RestPage;
import com.leaniot.domain.Device;
import com.leaniot.domain.DeviceType;
import com.leaniot.domain.IoTObject;
import com.leaniot.domain.User;
import com.leaniot.domain.attribute.AttTypeInfo;
import com.leaniot.domain.attribute.IDeviceAttTypeInfo;
import com.leaniot.dto.ActionTypeInfo;
import com.leaniot.dto.DeviceTypeInfo;
import com.leaniot.dto.DeviceTypeRenameInfo;
import com.leaniot.dto.PageInfo;
import com.leaniot.dto.UserInfo;
import com.leaniot.dto.UserUpdateInfo;
import com.leaniot.exception.ValueException;

/**
 * 客户端与物联网平台的http会话
 *
 * @author 曹新宇
 */
@Component
public class HttpClientSession extends HttpSession {
	/**
	 * 获取设备信息。
	 * @param deviceId 设备标识符。
	 * @return 返回设备信息。
	 */
	public Device getDevice(String deviceId) {
		return getEntity("/device/" + deviceId, null, Device.class);
	}
	
	/**
	 * 查询符合条件的设备列表。
	 * @param queryParams 查询条件。
	 * @return 设备列表。
	 */
	public List<Device> queryDeviceList(Map<String, String> queryParams){
		return getEntity("/devices/list", queryParams, new ParameterizedTypeReference<List<Device>>() {});
	}
	
	private Class<User> userType = User.class;
	private String userUrl = "/user";
	
	public User addUser(UserInfo info) {
		return postEntity(userUrl, info, userType);
	}
	
	public User getCurrentUser() {
		return getEntity(userUrl + "/me", null, userType);
	}
	
	public User getUser(String username) {
		if(username != null && !username.isEmpty()) {
			return getEntity(userUrl + "/" + username, null, userType);
		} else
			throw new ValueException("username can't be empty");
	}
	
	public void deleteUser(String userId) {
		if(userId != null && !userId.isEmpty())
			deleteEntity(userUrl + "/" + userId, null, userType);
		else
			throw new ValueException("userid can't be empty");
	}
	
	public Page<User> getUserPage(PageInfo info) {
		if(info == null)
			info = new PageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		
		return getEntity(userUrl + "s", queryParams, new ParameterizedTypeReference<RestPage<User>>() {});
	}
	
	public User updateUserArea(UserUpdateInfo info) {
		return patchEntity(userUrl, info, userType);
	}
	
	private Class<DeviceType> deviceTypeType = DeviceType.class;
	private String deviceTypeUrl = "/" + IoTObject.devicetype.getName();
	
	public DeviceType addDevicetype(DeviceTypeInfo info) {
		return postEntity(deviceTypeUrl, info, deviceTypeType);
	}
	
	public DeviceType getDeviceType(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(deviceTypeUrl + "/" + id, null, deviceTypeType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	public Page<DeviceType> getDeviceTypePage(PageInfo info) {
		if(info == null)
			info = new PageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		
		return getEntity(deviceTypeUrl + "s/page", queryParams, new ParameterizedTypeReference<RestPage<DeviceType>>() {});
	}
	
	public List<DeviceType> getDeviceTypeList() {
		return getEntity(deviceTypeUrl + "s/list", null, new ParameterizedTypeReference<List<DeviceType>>() {});
	}
	
	public DeviceType renameDeviceType(DeviceTypeRenameInfo info) {
		return patchEntity(deviceTypeUrl + "/name", info, deviceTypeType);
	}
	
	public void deleteDeviceType(String id) {
		if(id != null && !id.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id, null, deviceTypeType);
		else
			throw new ValueException("id can't be empty");
	}
	
	public DeviceType addDevicetypeAttribute(String id, IDeviceAttTypeInfo info) {
		return postEntity(deviceTypeUrl + "/" + id + "/attribute", info, deviceTypeType);
	}
	
	public void deleteDeviceTypeAttribute(String id, String attribute) {
		if(id != null && !id.isEmpty() && attribute != null && !attribute.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id + "/attribute/" + attribute, null, deviceTypeType);
		else
			throw new ValueException("id and attribute can't be empty");
	}
	
	public DeviceType addDevicetypeActiontype(String id, ActionTypeInfo info) {
		return postEntity(deviceTypeUrl + "/" + id + "/actiontype", info, deviceTypeType);
	}
	
	public void deleteDeviceTypeActiontype(String id, String actiontype) {
		if(id != null && !id.isEmpty() && actiontype != null && !actiontype.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id + "/actiontype/" + actiontype, null, deviceTypeType);
		else
			throw new ValueException("id and actiontype can't be empty");
	}
	
	public DeviceType addDevicetypeAlarmtype(String id, AttTypeInfo info) {
		return postEntity(deviceTypeUrl + "/" + id + "/alarmtype", info, deviceTypeType);
	}
	
	public void deleteDeviceTypeAlarmtype(String id, String alarmtype) {
		if(id != null && !id.isEmpty() && alarmtype != null && !alarmtype.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id + "/alarmtype/" + alarmtype, null, deviceTypeType);
		else
			throw new ValueException("id and alarmtype can't be empty");
	}
}
