package com.pcjz.lems.business.entity;

import java.util.List;

/**
 * Created by ${YGP} on 2017/5/12.
 */

public class ProjectTreeMultiInfo {
    private String isSealed;
    private String createUserId;
    private String organizationName;
    private int level;
    private String updateUserId;
    private String isEntity;
    private String updateTime;
    private String idTree;
    private List<ProjectTreeMultiInfo> list;
    private String isLeaf;
    private String organizationType;
    private String createTime;
    private String isEnabled;
    private String tenantId;
    private String id;
    private String treeCode;
    private String nameTree;
    private String organizationManager;

    private String periodName;
    private String buildingUnit;
    private int totalBuildings;
    private String roomUnit;
    private String projectManagerId;
    private String location;
    private String comId;
    private String projectId;
    private String floorUnit;
    private String linker;
    private String parentId;
    private String corporateId;
    private String linkerPhone;
    private String projectStatus;

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getIsSealed() {
        return isSealed;
    }

    public void setIsSealed(String isSealed) {
        this.isSealed = isSealed;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getIsEntity() {
        return isEntity;
    }

    public void setIsEntity(String isEntity) {
        this.isEntity = isEntity;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getIdTree() {
        return idTree;
    }

    public void setIdTree(String idTree) {
        this.idTree = idTree;
    }

    /*public List<String> getProjectTreeChildInfoList() {
        return projectTreeChildInfoList;
    }

    public void setProjectTreeChildInfoList(List<String> projectTreeChildInfoList) {
        this.projectTreeChildInfoList = projectTreeChildInfoList;
    }*/


    public String getIsLeaf() {
        return isLeaf;
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    public String getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTreeCode() {
        return treeCode;
    }

    public void setTreeCode(String treeCode) {
        this.treeCode = treeCode;
    }

    public String getNameTree() {
        return nameTree;
    }

    public void setNameTree(String nameTree) {
        this.nameTree = nameTree;
    }

    public String getOrganizationManager() {
        return organizationManager;
    }

    public void setOrganizationManager(String organizationManager) {
        this.organizationManager = organizationManager;
    }

    public String getPeriodName() {
        return periodName;
    }

    public void setPeriodName(String periodName) {
        this.periodName = periodName;
    }

    public String getBuildingUnit() {
        return buildingUnit;
    }

    public void setBuildingUnit(String buildingUnit) {
        this.buildingUnit = buildingUnit;
    }

    public int getTotalBuildings() {
        return totalBuildings;
    }

    public void setTotalBuildings(int totalBuildings) {
        this.totalBuildings = totalBuildings;
    }

    public String getRoomUnit() {
        return roomUnit;
    }

    public void setRoomUnit(String roomUnit) {
        this.roomUnit = roomUnit;
    }

    public String getProjectManagerId() {
        return projectManagerId;
    }

    public void setProjectManagerId(String projectManagerId) {
        this.projectManagerId = projectManagerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getComId() {
        return comId;
    }

    public void setComId(String comId) {
        this.comId = comId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getFloorUnit() {
        return floorUnit;
    }

    public void setFloorUnit(String floorUnit) {
        this.floorUnit = floorUnit;
    }

    public String getLinker() {
        return linker;
    }

    public void setLinker(String linker) {
        this.linker = linker;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCorporateId() {
        return corporateId;
    }

    public void setCorporateId(String corporateId) {
        this.corporateId = corporateId;
    }

    public String getLinkerPhone() {
        return linkerPhone;
    }

    public void setLinkerPhone(String linkerPhone) {
        this.linkerPhone = linkerPhone;
    }

    public List<ProjectTreeMultiInfo> getList() {
        return list;
    }

    public void setList(List<ProjectTreeMultiInfo> list) {
        this.list = list;
    }
}
