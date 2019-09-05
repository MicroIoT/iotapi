package top.microiot.api.client;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import top.microiot.api.HttpSession;
import top.microiot.api.HttpSessionProperties;
import top.microiot.api.dto.RestPage;
import top.microiot.domain.Alarm;
import top.microiot.domain.Device;
import top.microiot.domain.DeviceType;
import top.microiot.domain.Domain;
import top.microiot.domain.Event;
import top.microiot.domain.Site;
import top.microiot.domain.SiteType;
import top.microiot.domain.User;
import top.microiot.domain.attribute.AttTypeInfo;
import top.microiot.domain.attribute.AttValueInfo;
import top.microiot.domain.attribute.AttributeType;
import top.microiot.domain.attribute.ClassTypeInfo;
import top.microiot.domain.attribute.IDeviceAttTypeInfo;
import top.microiot.dto.ActionTypeInfo;
import top.microiot.dto.AlarmPageInfo;
import top.microiot.dto.DeviceAddGroupInfo;
import top.microiot.dto.DeviceInfo;
import top.microiot.dto.DeviceMoveInfo;
import top.microiot.dto.DevicePageInfo;
import top.microiot.dto.DeviceRenameInfo;
import top.microiot.dto.DeviceTypeInfo;
import top.microiot.dto.DeviceTypeRenameInfo;
import top.microiot.dto.DeviceUpdateInfo;
import top.microiot.dto.DomainInfo;
import top.microiot.dto.DomainRenameInfo;
import top.microiot.dto.EventPageInfo;
import top.microiot.dto.NotificationPageInfo;
import top.microiot.dto.PageInfo;
import top.microiot.dto.QueryInfo;
import top.microiot.dto.SiteInfo;
import top.microiot.dto.SitePageInfo;
import top.microiot.dto.SiteRenameInfo;
import top.microiot.dto.SiteTypeRenameInfo;
import top.microiot.dto.SiteUpdateInfo;
import top.microiot.dto.UserInfo;
import top.microiot.dto.UserUpdateInfo;
import top.microiot.exception.NotFoundException;
import top.microiot.exception.ValueException;

/**
 * 客户端与物联网平台的http会话类
 *
 * @author 曹新宇
 */
@Component
public class HttpClientSession extends HttpSession {
	public HttpClientSession(HttpSessionProperties httpSessionProperties) {
		super(httpSessionProperties);
	}

	@Override
	protected void setLoginInfo(MultiValueMap<String, String> map) {
		super.setLoginInfo(map);
		String domain = httpSessionProperties.getDomain();
		if(domain != null && domain.length() > 0)
			map.add("domain", httpSessionProperties.getDomain());
	}

	/**
	 * 查询符合条件的设备列表。
	 * @param queryParams 查询条件。
	 * @return 设备列表。
	 */
	public List<Device> queryDeviceList(Map<String, String> queryParams){
		return getEntity("/devices/list", queryParams, new ParameterizedTypeReference<List<Device>>() {});
	}
	
	public static Class<Domain> domainType = Domain.class;
	public static String domainUrl = "/domains";
	
	/**
	 * 添加领域。
	 * @param info 被添加的领域信息。
	 * @return 返回添加成功的领域。
	 */
	public Domain addDomain(DomainInfo info) {
		return postEntity(domainUrl, info, domainType);
	}
	
	/**
	 * 获取当前登录的领域的信息。
	 * @return 返回当前登录的领域信息。
	 */
	public Domain getCurrentDomain() {
		return getEntity(domainUrl, null, domainType);
	}
	
	/**
	 * 删除指定领域。
	 * @param id 指定领域的标识符
	 */
	public void deleteDomain(String id) {
		if(id != null && !id.isEmpty())
			deleteEntity(domainUrl + "/" + id, null, domainType);
		else
			throw new ValueException("domain id can't be empty");
	}
	
	/**
	 * 获取指定领域的信息。
	 * @param id 指定领域的标识符
	 * @return 返回指定领域信息。
	 */
	public Domain getDomainById(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(domainUrl + "/id/" + id, null, domainType);
		} else
			throw new ValueException("domain id can't be empty");
	}
	
	/**
	 * 获取指定领域的信息。
	 * @param name 指定领域的名称
	 * @return 返回指定领域信息。
	 */
	public Domain getDomainByName(String name) {
		if(name != null && !name.isEmpty()) {
			return getEntity(domainUrl + "/name/" + name, null, domainType);
		} else
			throw new ValueException("domain name can't be empty");
	}
	
	/**
	 * 修改领域名称。
	 * @param info 修改领域的名称，包括领域标识符和新名称。
	 * @return 返回领域信息。
	 */
	public Domain renameDomain(DomainRenameInfo info) {
		return patchEntity(domainUrl, info, domainType);
	}
	
	/**
	 * 获取当前用户可以访问的领域列表。
	 * @return 返回可以访问的领域列表。
	 */
	public List<Domain> getMyDomains() {
		return getEntity(domainUrl  +"/me", null, new ParameterizedTypeReference<List<Domain>>() {});
	}
	public static Class<User> userType = User.class;
	public static String userUrl = "/users";
	
	
	/**
	 * 添加平台用户。
	 * @param info 被添加的用户信息。
	 * @return 返回添加成功的用户。
	 */
	public User addUser(UserInfo info) {
		return postEntity(userUrl, info, userType);
	}
	
	/**
	 * 获取当前登录用户的信息。
	 * @return 返回当前用户信息。
	 */
	public User getCurrentUser() {
		return getEntity(userUrl + "/me", null, userType);
	}
	
	/**
	 * 获取指定用户的信息。
	 * @param username 指定用户的用户名
	 * @return 返回指定用户信息。
	 */
	public User getUser(String username) {
		if(username != null && !username.isEmpty()) {
			return getEntity(userUrl + "/" + username, null, userType);
		} else
			throw new ValueException("username can't be empty");
	}
	
	/**
	 * 删除指定用户。
	 * @param userId 指定用户的标识符
	 */
	public void deleteUser(String userId) {
		if(userId != null && !userId.isEmpty())
			deleteEntity(userUrl + "/" + userId, null, userType);
		else
			throw new ValueException("userid can't be empty");
	}
	
	/**
	 * 获取系统用户列表页。
	 * @param info 指定页查询信息
	 * @return 返回指定页的用户。
	 */
	public Page<User> getUserPage(PageInfo info) {
		if(info == null)
			info = new PageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		
		return getEntity(userUrl , queryParams, new ParameterizedTypeReference<RestPage<User>>() {});
	}
	
	/**
	 * 修改区域管理员用户负责的区域。
	 * @param info 用户负责的区域的信息，包括用户标识符和区域标识符列表。
	 * @return 返回用户信息。
	 */
	public User updateUserArea(UserUpdateInfo info) {
		return patchEntity(userUrl, info, userType);
	}
	
	public static Class<DeviceType> deviceTypeType = DeviceType.class;
	public static String deviceTypeUrl = "/" +getIoTObjectName(DeviceType.class);
	
	/**
	 * 添加设备类型。
	 * @param info 设备类型信息，包括动态属性信息，静态属性信息，告警类型信息，操作类型信息，是否是设备组。
	 * @return 返回添加成功的设备类型。
	 */
	public DeviceType addDevicetype(DeviceTypeInfo info) {
		return postEntity(deviceTypeUrl, info, deviceTypeType);
	}
	
	/**
	 * 获取指定设备类型的信息。
	 * @param id 设备类型标识符。
	 * @return 返回指定设备类型。
	 */
	public DeviceType getDeviceType(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(deviceTypeUrl + "/" + id, null, deviceTypeType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 获取指定设备类型的信息。
	 * @param name 设备类型名称。
	 * @return 返回指定设备类型。
	 */
	public DeviceType getDevicetypeByName(String name) {
		QueryInfo q = new QueryInfo();
		String filter = String.format("{\"name\": \"%s\"}", name);
		q.setFilter(filter);
		DeviceType dt = this.getOneEntity(DeviceType.class, q);
		if(dt == null)
			throw new NotFoundException("device type");
		return dt;
	}
	
	/**
	 * 获取设备类型列表页。
	 * @param info 指定页查询信息
	 * @return 返回指定页的设备类型。
	 */
	public Page<DeviceType> getDeviceTypePage(PageInfo info) {
		if(info == null)
			info = new PageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		
		return getEntity(deviceTypeUrl + "/page", queryParams, new ParameterizedTypeReference<RestPage<DeviceType>>() {});
	}
	
	/**
	 * 获取设备类型列表。
	 * @return 返回设备类型列表。
	 */
	public List<DeviceType> getDeviceTypeList() {
		return getEntity(deviceTypeUrl + "/list", null, new ParameterizedTypeReference<List<DeviceType>>() {});
	}
	
	/**
	 * 修改设备类型名称。
	 * @param info 设备类型修改信息，包括设备类型标识符和新的名称和描述。
	 * @return 返回设备类型。
	 */
	public DeviceType renameDeviceType(DeviceTypeRenameInfo info) {
		return patchEntity(deviceTypeUrl + "/name", info, deviceTypeType);
	}
	
	/**
	 *删除指定设备类型。
	 * @param id 设备类型标识符
	 */
	public void deleteDeviceType(String id) {
		if(id != null && !id.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id, null, deviceTypeType);
		else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 修改设备类型，添加动态属性。
	 * @param id 设备类型标识符
	 * @param info 新添加的动态属性信息
	 * @return 返回修改成功的设备类型。
	 */
	public DeviceType addDevicetypeAttribute(String id, IDeviceAttTypeInfo info) {
		return postEntity(deviceTypeUrl + "/" + id + "/attribute", info, deviceTypeType);
	}
	
	/**
	 * 修改设备类型，删除动态属性。
	* @param id 设备类型标识符
	 * @param attribute 要删除的动态属性名称
	 */
	public void deleteDeviceTypeAttribute(String id, String attribute) {
		if(id != null && !id.isEmpty() && attribute != null && !attribute.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id + "/attribute/" + attribute, null, deviceTypeType);
		else
			throw new ValueException("id and attribute can't be empty");
	}
	
	/**
	 * 修改设备类型，添加操作类型。
	 * @param id 设备类型标识符。
	 * @param info 新添加的操作类型信息。
	 * @return 返回修改成功的设备类型。
	 */
	public DeviceType addDevicetypeActiontype(String id, ActionTypeInfo info) {
		return postEntity(deviceTypeUrl + "/" + id + "/actiontype", info, deviceTypeType);
	}
	
	/**
	 * 修改设备类型，删除操作类型。
	* @param id 设备类型标识符。
	 * @param actiontype 要删除的操作类型名称。
	 */
	public void deleteDeviceTypeActiontype(String id, String actiontype) {
		if(id != null && !id.isEmpty() && actiontype != null && !actiontype.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id + "/actiontype/" + actiontype, null, deviceTypeType);
		else
			throw new ValueException("id and actiontype can't be empty");
	}
	
	/**
	 * 修改设备类型，添加告警类型。
	 * @param id 设备类型标识符。
	 * @param info 新添加的告警类型信息。
	 * @return 返回修改成功的设备类型。
	 */
	public DeviceType addDevicetypeAlarmtype(String id, AttTypeInfo info) {
		return postEntity(deviceTypeUrl + "/" + id + "/alarmtype", info, deviceTypeType);
	}
	
	/**
	 * 修改设备类型，删除告警类型。
	* @param id 设备类型标识符。
	 * @param alarmtype 要删除的告警类型名称。
	 */
	public void deleteDeviceTypeAlarmtype(String id, String alarmtype) {
		if(id != null && !id.isEmpty() && alarmtype != null && !alarmtype.isEmpty())
			deleteEntity(deviceTypeUrl + "/" + id + "/alarmtype/" + alarmtype, null, deviceTypeType);
		else
			throw new ValueException("id and alarmtype can't be empty");
	}
	
	public static Class<SiteType> siteTypeType = SiteType.class;
	public static String siteTypeUrl = "/" + getIoTObjectName(SiteType.class);
	
	/**
	 * 添加场地类型。
	 * @param info 场地类型信息，包括属性信息等。
	 * @return 返回添加成功的场地类型。
	 */
	public SiteType addSitetype(ClassTypeInfo info) {
		return postEntity(siteTypeUrl, info, siteTypeType);
	}
	
	/**
	 * 获取指定场地类型的信息。
	 * @param id 场地类型标识符。
	 * @return 返回指定场地类型。
	 */
	public SiteType getSiteType(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(siteTypeUrl + "/" + id, null, siteTypeType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 获取指定场地类型的信息。
	 * @param name 场地类型名称。
	 * @return 返回指定场地类型。
	 */
	public SiteType getSitetypeByName(String name) {
		QueryInfo q = new QueryInfo();
		String filter = String.format("{\"name\": \"%s\"}", name);
		q.setFilter(filter);
		SiteType st = this.getOneEntity(SiteType.class, q);
		if(st == null)
			throw new NotFoundException("site type");
		return st;
	}

	/**
	 * 获取场地类型列表页。
	 * @param info 指定页查询信息
	 * @return 返回指定页的场地类型。
	 */
	public Page<SiteType> getSiteTypePage(PageInfo info) {
		if(info == null)
			info = new PageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		
		return getEntity(siteTypeUrl + "/page", queryParams, new ParameterizedTypeReference<RestPage<SiteType>>() {});
	}
	
	/**
	 * 获取场地类型列表。
	 * @return 返回场地类型列表。
	 */
	public List<SiteType> getSiteTypeList() {
		return getEntity(siteTypeUrl + "/list", null, new ParameterizedTypeReference<List<SiteType>>() {});
	}
	
	/**
	 * 修改场地类型名称。
	 * @param info 场地类型修改信息，包括场地类型标识符和新的名称和描述。
	 * @return 返回场地类型。
	 */
	public SiteType renameSiteType(SiteTypeRenameInfo info) {
		return patchEntity(siteTypeUrl + "/name", info, siteTypeType);
	}
	
	/**
	 *删除指定场地类型。
	 * @param id 场地类型标识符
	 */
	public void deleteSiteType(String id) {
		if(id != null && !id.isEmpty())
			deleteEntity(siteTypeUrl + "/" + id, null, siteTypeType);
		else
			throw new ValueException("id can't be empty");
	}
	
	public static Class<Site> siteType = Site.class;
	public static String siteUrl = "/" + getIoTObjectName(Site.class);
	
	/**
	 * 添加场地。
	 * @param info 场地信息，包括场地名称，场地类型，所属场地，属性值等。
	 * @return 返回添加成功的场地。
	 */
	public Site addSite(SiteInfo<Object> info) {
		SiteType st = getSitetypeByName(info.getSiteType());
		
		SiteInfo<AttValueInfo> siteValue = new SiteInfo<AttValueInfo>();
		siteValue.setName(info.getName());
		siteValue.setLocationId(info.getLocationId());
		siteValue.setSiteType(info.getSiteType());
		Map<String, Object> attInfos = info.getAttInfos();
		Map<String, AttributeType> attDefinition = st.getAttDefinition();
		
		Map<String, AttValueInfo> attValues = getAttInfos(attInfos, attDefinition);
		siteValue.setAttInfos(attValues);
		
		return postEntity(siteUrl, siteValue, siteType);
	}

	private Map<String, AttValueInfo> getAttInfos(Map<String, Object> attInfos,
			Map<String, ? extends AttributeType> attDefinition) {
		Map<String, AttValueInfo> attValue = new HashMap<String, AttValueInfo>();
		
		for (Map.Entry<String, ? extends AttributeType> entry : attDefinition.entrySet()) {
			String key = entry.getKey();
			AttributeType attType = entry.getValue();
			Object value = attInfos.get(key);
			
			try{
				attValue.put(key, attType.getAttValue(value));
			} catch(ValueException e) {
				throw new ValueException(key + ":" + e.getMessage());
			}
		}
		return attValue;
	}
	
	/**
	 * 获取指定场地的信息。
	 * @param id 场地标识符。
	 * @return 返回指定场地。
	 */
	public Site getSite(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(siteUrl + "/" + id, null, siteType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 获取当前登录用户（区域管理员）负责的所有场地的信息。
	 * @return 返回场地列表。
	 */
	public List<Site> getMySites() {
		return getEntity(siteUrl + "/me", null, new ParameterizedTypeReference<List<Site>>() {});
	}
	
	/**
	 *删除指定场地。
	 * @param id 场地标识符
	 */
	public void deleteSite(String id) {
		if(id != null && !id.isEmpty())
			deleteEntity(siteUrl + "/" + id, null, siteType);
		else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 修改场地名称。
	 * @param info 场地修改信息，包括场地标识符和新的名称。
	 * @return 返回场地。
	 */
	public Site renameSite(SiteRenameInfo info) {
		return patchEntity(siteUrl + "/name", info, siteType);
	}
	
	/**
	 * 修改场地属性。
	 * @param info 场地修改信息，包括场地标识符，场地属性信息。
	 * @return 返回修改成功的场地。
	 */
	public Site updateSite(SiteUpdateInfo<Object> info) {
		SiteType st = getSite(info.getId()).getSiteType();
		
		SiteUpdateInfo<AttValueInfo> siteValue = new SiteUpdateInfo<AttValueInfo>();
		siteValue.setId(info.getId());
		Map<String, Object> attInfos = info.getAttInfos();
		Map<String, AttributeType> attDefinition = st.getAttDefinition();
		
		Map<String, AttValueInfo> attValues = getAttInfos(attInfos, attDefinition);
		siteValue.setAttInfos(attValues);
		
		return patchEntity(siteUrl, siteValue, siteType);
	}
	
	/**
	 * 获取场地列表页。
	 * @param info 指定页查询信息以及查询条件，包括父场地，场地类型，场地名称。
	 * @return 返回指定页的场地。
	 */
	public Page<Site> getSitePage(SitePageInfo info) {
		if(info == null)
			info = new SitePageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		if(info.getLocationId() != null)
			queryParams.put("locationId", info.getLocationId());
		if(info.getName() != null)
			queryParams.put("name", info.getName());
		if(info.getSiteTypeId() != null)
			queryParams.put("siteTypeId", info.getSiteTypeId());
		
		return getEntity(siteUrl , queryParams, new ParameterizedTypeReference<RestPage<Site>>() {});
	}
	
	/**
	 * 获取符合条件的场地数量。
	 * @param parentId 父场地标识符。
	 * @param siteName 场地名称。
	 * @param siteTypeId 场地类型标识符。
	 * @return 返回符合条件的场地数量。
	 */
	public long getSiteCount(String parentId, String siteName, String siteTypeId) {
		Map<String, String> queryParams= new HashMap<String, String>();
		if(parentId != null)
			queryParams.put("parentId", parentId);
		if(siteName != null)
			queryParams.put("siteName", siteName);
		if(siteTypeId != null)
			queryParams.put("siteTypeId", siteTypeId);
		
		return getEntity(siteUrl + "/count", queryParams, Long.class);
	}
	
	public static Class<Device> deviceType = Device.class;
	public static String deviceUrl = "/" + getIoTObjectName(Device.class);
	
	/**
	 * 添加设备。
	 * @param info 设备信息，包括设备名称，设备类型，设备标识符，设备所属场地标识符，属性值等。
	 * @return 返回添加成功的设备。
	 */
	public Device addDevice(DeviceInfo<Object> info) {
		DeviceType dt = getDevicetypeByName(info.getDeviceType());
		
		DeviceInfo<AttValueInfo> deviceValue = new DeviceInfo<AttValueInfo>();
		deviceValue.setDeviceType(info.getDeviceType());
		deviceValue.setName(info.getName());
		deviceValue.setLocationId(info.getLocationId());
		Map<String, Object> attInfos = info.getAttInfos();
		Map<String, AttributeType> attDefinition = dt.getStaticAttDefinition();
		
		Map<String, AttValueInfo> attValues = getAttInfos(attInfos, attDefinition);
		deviceValue.setAttInfos(attValues);
		
		return postEntity(deviceUrl, deviceValue, deviceType);
	}
	
	/**
	 * 获取指定设备的信息。
	 * @param id 设备标识符。
	 * @return 返回指定设备。
	 */
	public Device getDevice(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(deviceUrl + "/" + id, null, deviceType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 获取当前登录用户（区域管理员）负责的所有设备的信息。
	 * @return 返回设备列表。
	 */
	public List<Device> getMyDevices() {
		return getEntity(deviceUrl + "/area", null, new ParameterizedTypeReference<List<Device>>() {});
	}
	
	/**
	 * 修改设备名称。
	 * @param info 设备修改信息，包括设备标识符和新的名称。
	 * @return 返回设备。
	 */
	public Device renameDevice(DeviceRenameInfo info) {
		return patchEntity(deviceUrl + "/name", info, deviceType);
	}
	
	/**
	 * 修改设备静态属性。
	 * @param info 设备修改信息，包括设备标识符，设备静态属性信息。
	 * @return 返回修改成功的设备。
	 */
	public Device updateDevice(DeviceUpdateInfo<Object> info) {
		DeviceType st = getDevice(info.getId()).getDeviceType();
		
		DeviceUpdateInfo<AttValueInfo> deviceValue = new DeviceUpdateInfo<AttValueInfo>();
		deviceValue.setId(info.getId());
		Map<String, Object> attInfos = info.getAttInfos();
		Map<String, AttributeType> attDefinition = st.getStaticAttDefinition();
		
		Map<String, AttValueInfo> attValues = getAttInfos(attInfos, attDefinition);
		deviceValue.setAttInfos(attValues);
		
		return patchEntity(deviceUrl, deviceValue, deviceType);
	}
	
	/**
	 * 修改设备的场地。
	 * @param info 设备修改信息，包括设备标识符，新场地标识符。
	 * @return 返回修改成功的设备。
	 */
	public Device moveDevice(DeviceMoveInfo info) {
		return patchEntity(deviceUrl + "/site", info, deviceType);
	}
	
	/**
	 * 获取设备列表页。
	 * @param info 指定页查询信息以及查询条件，包括场地标识符，设备类型，设备名称。
	 * @return 返回指定页的设备。
	 */
	public Page<Device> getDevicePage(DevicePageInfo info) {
		if(info == null)
			info = new DevicePageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		if(info.getLocationId() != null)
			queryParams.put("locationId", info.getLocationId());
		if(info.getName() != null)
			queryParams.put("name", info.getName());
		if(info.getDeviceTypeId() != null)
			queryParams.put("deviceTypeId", info.getDeviceTypeId());
		
		return getEntity(deviceUrl , queryParams, new ParameterizedTypeReference<RestPage<Device>>() {});
	}
	
	/**
	 * 获取符合条件的设备数量。
	 * @param locationId 场地标识符。
	 * @param name 设备名称。
	 * @param deviceTypeId 设备类型标识符。
	 * @return 返回符合条件的设备数量。
	 */
	public long getDeviceCount(String locationId, String name, String deviceTypeId) {
		Map<String, String> queryParams= new HashMap<String, String>();
		if(locationId != null)
			queryParams.put("locationId", locationId);
		if(name != null)
			queryParams.put("name", name);
		if(deviceTypeId != null)
			queryParams.put("deviceTypeId", deviceTypeId);
		
		return getEntity(deviceUrl + "/count", queryParams, Long.class);
	}
	
	/**
	 * 添加设备到设备组中。
	 * @param info 添加到设备组信息，包括被添加的设备id和设备组id。
	 * @return 返回添加成功的设备信息。
	 */
	public Device addGroup(DeviceAddGroupInfo info) {
		return postEntity(deviceUrl + "/group", info, deviceType);
	}
	
	/**
	 * 从设备组中移除设备。
	 * @param deviceId 被移除的设备id。
	 * @return 返回移除成功的设备信息。
	 */
	public Device removeGroup(String deviceId) {
		return deleteEntity(deviceUrl + "/group/" + deviceId, null, deviceType);
	}
	
	/**
	 * 获取指定设备的同级设备信息。
	 * @param deviceId 指定的设备id。
	 * @return 返回同级设备列表
	 */
	public List<Device> getSibling(String deviceId){
		return getEntity(deviceUrl + "/sibling/" + deviceId, null, new ParameterizedTypeReference<List<Device>>() {});
	}
	
	/**
	 * 获取指定设备的子设备信息
	 * @param deviceId 指定的设备id。
	 * @return 返回子设备列表
	 */
	public List<Device> getChildren(String deviceId){
		return getEntity(deviceUrl + "/children/" + deviceId, null, new ParameterizedTypeReference<List<Device>>() {});
	}
	public static Class<Alarm> alarmType = Alarm.class;
	public static String alarmUrl = "/" + getIoTObjectName(Alarm.class);
	
	/**
	 * 获取指定告警信息。
	 * @param id 告警标识符。
	 * @return 返回指定告警。
	 */
	public Alarm getAlarm(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(alarmUrl + "/" + id, null, alarmType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 获取告警列表页。
	 * @param info 指定页查询信息以及查询条件，包括告警接收时间，告警上报时间，告警类型，上报对象标识符。
	 * @return 返回指定页的告警。
	 */
	public Page<Alarm> getAlarmPage(AlarmPageInfo info) {
		SimpleDateFormat format = new SimpleDateFormat(NotificationPageInfo.FORMAT);
		if(info == null)
			info = new AlarmPageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		if(info.getNotifyObjectId() != null)
			queryParams.put("notifyObjectId", info.getNotifyObjectId());
		if(info.getReportFrom() != null)
			queryParams.put("reportFrom", format.format(info.getReportFrom()));
		if(info.getReportTo() != null)
			queryParams.put("reportTo", format.format(info.getReportTo()));
		if(info.getReceiveFrom() != null)
			queryParams.put("receiveFrom", format.format(info.getReceiveFrom()));
		if(info.getReceiveTo() != null)
			queryParams.put("receiveTo", format.format(info.getReceiveTo()));
		if(info.getAlarmType() != null)
			queryParams.put("alarmType", info.getAlarmType());
		
		return getEntity(alarmUrl, queryParams, new ParameterizedTypeReference<RestPage<Alarm>>() {});
	}
	
	public static Class<Event> eventType = Event.class;
	public static String eventUrl = "/" + getIoTObjectName(Event.class);
	
	/**
	 * 获取指定事件信息。
	 * @param id 事件标识符。
	 * @return 返回指定事件。
	 */
	public Event getEvent(String id) {
		if(id != null && !id.isEmpty()) {
			return getEntity(eventUrl + "/" + id, null, eventType);
		} else
			throw new ValueException("id can't be empty");
	}
	
	/**
	 * 获取事件列表页。
	 * @param info 指定页查询信息以及查询条件，包括事件接收时间，事件上报时间，属性名称，设备标识符。
	 * @return 返回指定页的事件。
	 */
	public Page<Event> getEventPage(EventPageInfo info) {
		SimpleDateFormat format = new SimpleDateFormat(NotificationPageInfo.FORMAT);
		if(info == null)
			info = new EventPageInfo();
		Map<String, String> queryParams= new HashMap<String, String>();
		queryParams.put("currentPage", Integer.toString(info.getCurrentPage()));
		queryParams.put("numPerPage", Integer.toString(info.getNumPerPage()));
		if(info.getDeviceId() != null)
			queryParams.put("deviceId", info.getDeviceId());
		if(info.getAttribute() != null)
			queryParams.put("attribute", info.getAttribute());
		if(info.getReportFrom() != null)
			queryParams.put("reportFrom", format.format(info.getReportFrom()));
		if(info.getReportTo() != null)
			queryParams.put("reportTo", format.format(info.getReportTo()));
		if(info.getReceiveFrom() != null)
			queryParams.put("receiveFrom", format.format(info.getReceiveFrom()));
		if(info.getReceiveTo() != null)
			queryParams.put("receiveTo", format.format(info.getReceiveTo()));
		
		return getEntity(eventUrl , queryParams, new ParameterizedTypeReference<RestPage<Event>>() {});
	}
}
