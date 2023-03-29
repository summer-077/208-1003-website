<template>
    <div v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL', 'FILE:ARCHIVE'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="searchHandle()">
            <el-form-item prop="creatorName">
                <el-input v-model="dataForm.creatorName" size="medium" placeholder="申请人" clearable="clearable" />
            </el-form-item>
            <el-form-item prop="type">
                <el-select v-model="dataForm.type" placeholder="类别" size="medium" clearable="clearable">
                    <el-option label="会议申请" value="会议申请"></el-option>
                    <el-option label="员工请假" value="员工请假"></el-option>
                    <el-option label="报销申请" value="报销申请"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item prop="instanceId">
                <el-input v-model="dataForm.instanceId" size="medium" placeholder="实例编号" clearable="clearable" />
            </el-form-item>
            <el-form-item>
                <el-button type="primary" size="medium" @click="searchHandle()">查询</el-button>
            </el-form-item>
            <el-form-item class="mold">
                <el-radio-group v-model="dataForm.status" size="medium" @change="searchHandle()">
                    <el-radio-button label="待审批"></el-radio-button>
                    <el-radio-button label="已审批"></el-radio-button>
                    <el-radio-button label="已结束"></el-radio-button>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <el-table
            ref="approvalTable"
            :data="dataList"
            :border="border"
            v-loading="dataListLoading"
            cell-style="padding: 4px 0"
            size="medium"
            style="width: 100%;"
            @expand-change="expand"
            :row-key="getRowKeys"
            :expand-row-keys="expands"
        >
            <el-table-column prop="remark" header-align="center" :align="center" type="expand">
                <template #default="scope">
                    <div class="content-container">
                        <el-row >
                            <el-col :span='6'>
                                <table class="content-table">
                                    <tbody v-if="scope.row.type == '会议申请'">
                                        <tr>
                                            <th><span>主题</span></th>
                                            <td :title="scope.row.title">{{ scope.row.title }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>内容</span></th>
                                            <td :title="content.desc">{{ content.desc }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>日期</span></th>
                                            <td>{{ content.date }}&nbsp;&nbsp;&nbsp;{{ content.start }} ~ {{ content.end }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>地点</span></th>
                                            <td>{{ content.place }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>参会人</span></th>
                                            <td :title="content.members">{{ content.members }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>审批结果</span></th>
                                            <td>
                                                <span v-if="scope.row.status != '已结束'">审批中</span>
                                                <span v-if="scope.row.status == '已结束' && scope.row.result == '同意'">
                                                    已同意
                                                </span>
                                                <span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
                                                    已拒绝
                                                </span>
                                            </td>

                                        </tr>
                                        <tr v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
                                            <th><span >驳回理由</span></th>
                                            <td>
                                                <span>{{content.reject}}</span>
                                                
                                            </td>
                                        </tr>
                                    </tbody>
                                    <tbody v-if="scope.row.type == '员工请假'">
                                        <tr>
                                            <th><span>请假原因</span></th>
                                            <td :title="content.reason">{{ content.reason }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>请假类型</span></th>
                                            <td>{{ content.type == 1 ? '病假' : '事假' }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>申请人</span></th>
                                            <td>{{ content.name }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>起始时间</span></th>
                                            <td>{{ content.start }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>截止时间</span></th>
                                            <td>{{ content.end }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>审批结果</span></th>
                                            <td>
                                                <span v-if="scope.row.status != '已结束'">审批中</span>
                                                <span v-if="scope.row.status == '已结束' && scope.row.result == '同意'">
                                                    已同意
                                                </span>
                                                <span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
                                                    已拒绝
                                                </span>
                                            </td>
                                        </tr>
                                        <tr v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
                                            <th><span >驳回理由</span></th>
                                            <td>
                                                <span>{{content.reject}}</span>
                                                
                                            </td>
                                        </tr>
                                    </tbody>
                                    
                                    <tbody v-if="scope.row.type == '报销申请'">
                                        <tr>
                                            <th><span>申请人</span></th>
                                            <td>{{ content.name }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>报销金额</span></th>
                                            <td>{{ content.amount }}元</td>
                                        </tr>
                                        <tr>
                                            <th><span>借款金额</span></th>
                                            <td>{{ content.anleihen }}元</td>
                                        </tr>
                                        <tr>
                                            <th><span>实际金额</span></th>
                                            <td>{{ content.balance }}元</td>
                                        </tr>
                                        <tr>
                                            <th><span>报销类型</span></th>
                                            <td>{{ content.type }}</td>
                                        </tr>
                                        <tr>
                                            <th><span>审批结果</span></th>
                                            <td>
                                                <span v-if="scope.row.status != '已结束'">审批中</span>
                                                <span v-if="scope.row.status == '已结束' && scope.row.result == '同意'">
                                                    已同意
                                                </span>
                                                <span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
                                                    已拒绝
                                                </span>
                                            </td>
                                        </tr>
                                        <tr v-if="scope.row.status == '已结束' && scope.row.result == '不同意'">
                                            <th><span >驳回理由</span></th>
                                            <td>
                                                <span>{{content.reject}}</span>
                                                
                                            </td>
                                        </tr>
                                    </tbody>
                                </table>
                            </el-col>
                            <!-- 这里是图片的位置！！！！！ -->
                            <el-col :span='8'>
                                <div 
                                style="height=240px"
                                >
                                <el-image
                                    class="archive"
                                    v-for="item in imgList" :key="item.url"
                                    :src="item.url"
                                    :preview-src-list="[item.url]"
                                    style="width:150px; height:150px;"
                                 
                                ></el-image>
                                </div>
                            </el-col>
                        
               <!-- v-if="content.hasOwnProperty('files')" -->
                            <!-- :src="content.files[0].url" -->
                                <!-- src="https://emos-1313348363.cos.ap-guangzhou.myqcloud.com/leaveinsert/d73d4f015d3242b29b61e11a4d28d0df.jpg" -->
                            <el-col :span='10'>   
                                <el-image class="bpmn" :src="bpmnUrl" :preview-src-list="bpmnList"></el-image>
                            </el-col>
                        </el-row>
                    </div>
                </template>
            </el-table-column>
            <el-table-column type="index" header-align="center" align="center" label="序号" width="100" />
            <el-table-column prop="title" header-align="center" align="center" label="审批事项" min-width="400" />
            <el-table-column prop="type" header-align="center" align="center" label="类别" min-width="180" />
            <el-table-column prop="creatorName" header-align="center" align="center" label="申请人" min-width="150" />
            <el-table-column prop="createDate" header-align="center" align="center" label="发起日期" min-width="180" />
            <el-table-column prop="status" header-align="center" align="center" label="状态" min-width="150">
                <template #default="scope">
                    <span v-if="scope.row.status != '已结束'" style="color: orange;">审批中</span>
                    <span v-if="scope.row.status == '已结束' && scope.row.result == '同意'" style="color: #17B3A3;">
                        已同意
                    </span>
                    <span v-if="scope.row.status == '已结束' && scope.row.result == '不同意'" style="color: #f56c6c;">
                        已拒绝
                    </span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        size="medium"
                        v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL']) && dataForm.status == '待审批' && !scope.row.filing"
                        @click="disapproveHandle(scope.row)"
                    >
                        否决
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        v-if="isAuth(['ROOT', 'WORKFLOW:APPROVAL']) && dataForm.status == '待审批' && !scope.row.filing"
                        @click="approveHandle(scope.row)"
                    >
                        同意
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        v-if="dataForm.status != '待审批'"
                        @click="viewHandle(scope.row)"
                    >
                        查看
                    </el-button>
                    <el-button
                        type="text"
                        size="medium"
                        v-if="isAuth(['ROOT', 'FILE:ARCHIVE'])&&scope.row.status=='待审批' "
                        @click="archive(scope.row)"
                    >
                        归档
                    </el-button>
                    <!-- v-if="isAuth(['ROOT', 'FILE:ARCHIVE'])&& !scope.row.filing" -->
            
                </template>
            </el-table-column>
        </el-table>
        <!-- 这里是分页控件！！！！点击这里可以变换显示 -->
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="totalCount"
            layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
        <archive v-if="archiveVisible" ref="archive" @refreshDataList="loadDataList"></archive>
        <refuse v-if="refuseVisible"  ref="refuse" @refreshDataList="loadDataList"></refuse>
    
    </div>
</template>








<script>
import Archive from './archive.vue';
import pic from './leave-add-pic.vue'
import refuse from './approval_refuse.vue'
export default {
	components: {
		Archive,
		refuse,
		pic
	},
	data: function() {
		return {
			imgList:[],
			picVisible:false,
			refuseVisible:false,
			pageIndex: 1,
			pageSize: 10,
			totalPage: 0,
			dataListLoading: false,
			dataListSelections: [],
			archiveVisible: false,
			dataForm: {
				creatorName: null,
				type: null,
				status: '待审批',
				instanceId: null
			},
			dataList: [],
			content: {},
			bpmnUrl: null,
			bpmnList: [],
			archiveList: [],
			dataRule: {
				creatorName: [{ required: false, pattern: '^[\u4e00-\u9fa5]{2,20}$', message: '姓名格式错误' }],
				instanceId: [
					{
						required: false,
						pattern: '^[0-9A-Za-z\\-]{36}$',
						message: '实例编号格式错误'
					}
				]
			},
			expands: [],
			getRowKeys(row) {
				return row.taskId;
			}
		};
	},
	methods: {
		// 获取数据列表
		loadDataList: function() {
	
			let that = this;
			that.dataListLoading = true;
			let data = {
				creatorName: that.dataForm.creatorName,
				type: that.dataForm.type,
				status: that.dataForm.status,
				instanceId: that.dataForm.instanceId,
				page: that.pageIndex,
				length: that.pageSize
			};
			that.$http('approval/searchTaskByPage', 'POST', data, true, function(resp) {
		
				console.log("approvalresp",resp);
				let page = resp.page;
				that.dataList = page.list;
				console.log("page.list",page.list);
				that.totalCount = page.totalCount;
				that.dataListLoading = false;
			});
		},
			sizeChangeHandle: function(val) {
			this.pageSize = val;
			this.pageIndex = 1;
			this.loadDataList();
		},
		currentChangeHandle: function(val) {
			this.pageIndex = val;
			this.loadDataList();
		},
		searchHandle: function() {
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.$refs['dataForm'].clearValidate();
					if (this.dataForm.creatorName == '') {
						this.dataForm.creatorName = null;
					}
					if (this.dataForm.instanceId == '') {
						this.dataForm.instanceId = null;
					}
					if (this.pageIndex != 1) {
						this.pageIndex = 1;
					}
					this.loadDataList();
				} else {
					return false;
				}
			});
		},
		expand: function(row, expandedRows) {
		
			let that = this;
			if (expandedRows.length > 0) {
				that.expands = [];
				if (row) {
					that.expands.push(row.taskId);
					let data = {
						instanceId: row.processId,
						type: row.type,
						status: row.status
					};
					
					that.$http('approval/searchApprovalContent', 'POST', data, false, function(resp) {
						let content = resp.content;
						that.content = content;
						that.imgList=content.files;
						console.log("content",that.content);
						if (content.hasOwnProperty('files')) {
							for (let one of content.files) {
								that.archiveList.push(one.url);
							}
							console.log("archiveList",that.archiveList);
						}
            console.log('row.type',row.type);
			console.log('content',content);
						if (row.type == '报销申请') {
							if (content.typeId == 1) {
								content.type = '普通报销';
							} else if (content.typeId == 2) {
								content.type = '差旅报销';
							}
						}
					
					});
					
					
						if (expandedRows.length>0) {
							that.bpmnUrl = this.$baseUrl +'approval/searchApprovalBpmn' +
										'?instanceId='+row.processId + "&token="+ localStorage.getItem("token")+
										'&time=' + new Date().getTime();
							that.bpmnList = [that.bpmnUrl];
					}

				}
			} else {
				that.expands = [];
			}
		},
		approve: function(row, approval) {
			let that = this;
			console.log("row",row)
			that.dataListLoading = true;
			let data = {
				taskId: row.taskId,
				approval: approval,
				instanceId:row.processId,
				variety:row.type
			};
			that.$http('approval/approvalTask', 'POST', data, true, function(resp) {

				that.pageIndex = 1;
				that.loadDataList();
			});
		},
		approveHandle: function(row) {
			let that = this;
			console.log("row",row)
			that.dataListLoading = true;
			let data = {
				taskId: row.taskId,
				approval: "同意",
				instanceId:row.processId,
				variety:row.type
			};
			that.$http('approval/approvalTask', 'POST', data, true, function(resp) {

				that.pageIndex = 1;
				that.loadDataList();
			});
			this.$message({
				message:'已同意',
				type:'success'
			});
		
		},
		disapproveHandle: function(row)
		{
			console.log("taskId",row.taskId)
			console.log("approw",row);
			let that=this;
            this.refuseVisible=true
			 this.$nextTick(()=>{
			this.$refs.refuse.init()
			this.$refs["refuse"].id=row.processId
			//把驳回理由当中的taskId赋值
				this.$refs["refuse"].taskId=row.taskId
				this.$refs["refuse"].variety=row.type
			 })
			 	// that.approve(row, '不同意');
		},
		viewHandle: function(row) {
			this.$refs.approvalTable.toggleRowExpansion(row, true);
		},
		//归档，调用此方法之后，调用init当中的init
		archive: function(taskId) {
            let that =this
			// this.archiveVisible =true;
			// this.$nextTick(() => {
			// 	this.$refs.archive.init(taskId);
			// });
            console.log("taskid",taskId);
           let data={
				taskId:taskId.taskId,
				// files:JSON.stringify(files)//json数组转化为字符串
			}
			that.$http("approval/archiveTask","POST",data,true,function(resp){
				that.$message({
					message: '操作成功',
					type: 'success',
					duration: 1200,
				});
				that.loadDataList()
            })
		
	},
    },
	created: function() {
		this.loadDataList();
	},
	
};
</script>

<style lang="less" scoped="scoped">
@import url('approval.less');
</style>
