package com.kyhsgeekcode.dereinfo.dereclient

/*
using System;
using System.Collections.Generic;
using System.Text;
using LitJson;
using MsgPack;
using Stage;
using UnityEngine;

namespace Cute
{
	// Token: 0x020003AC RID: 940
	public class NetworkTask
	{
		// Token: 0x060020C6 RID: 8390 RVA: 0x0009996C File Offset: 0x00097B6C
		public NetworkTask()
		{
			this.ResponseData = null;
		}

		// Token: 0x170005D8 RID: 1496
		// (get) Token: 0x060020C7 RID: 8391 RVA: 0x00099994 File Offset: 0x00097B94
		// (set) Token: 0x060020C8 RID: 8392 RVA: 0x000999CC File Offset: 0x00097BCC
		public string Url
		{
			get
			{
				string arg = string.Empty;
				arg = ApiType.ApiList[this.type];
				return string.Format("{0}{1}", CustomPreference.GetApplicationServerURL(), arg);
			}
			private set
			{
			}
		}

		// Token: 0x170005D9 RID: 1497
		// (get) Token: 0x060020C9 RID: 8393 RVA: 0x000999D0 File Offset: 0x00097BD0
		// (set) Token: 0x060020CA RID: 8394 RVA: 0x000999D8 File Offset: 0x00097BD8
		public bool Indicator { get; set; }

		// Token: 0x170005DA RID: 1498
		// (get) Token: 0x060020CB RID: 8395 RVA: 0x000999E4 File Offset: 0x00097BE4
		// (set) Token: 0x060020CC RID: 8396 RVA: 0x000999EC File Offset: 0x00097BEC
		public Action<NetworkTask.ResultCode> CallbackOnSuccess { get; set; }

		// Token: 0x170005DB RID: 1499
		// (get) Token: 0x060020CD RID: 8397 RVA: 0x000999F8 File Offset: 0x00097BF8
		// (set) Token: 0x060020CE RID: 8398 RVA: 0x00099A00 File Offset: 0x00097C00
		public Action<NetworkTask.ResultCode> CallbackOnFailure { get; set; }

		// Token: 0x170005DC RID: 1500
		// (get) Token: 0x060020CF RID: 8399 RVA: 0x00099A0C File Offset: 0x00097C0C
		// (set) Token: 0x060020D0 RID: 8400 RVA: 0x00099A14 File Offset: 0x00097C14
		public Action<int> CallbackOnResultCodeError { get; set; }

		// Token: 0x170005DD RID: 1501
		// (get) Token: 0x060020D1 RID: 8401 RVA: 0x00099A20 File Offset: 0x00097C20
		public Dictionary<string, string> Header
		{
			get
			{
				return this.header;
			}
		}

		// Token: 0x170005DE RID: 1502
		// (get) Token: 0x060020D2 RID: 8402 RVA: 0x00099A28 File Offset: 0x00097C28
		public byte[] Body
		{
			get
			{
				return this.body;
			}
		}

		// Token: 0x170005DF RID: 1503
		// (get) Token: 0x060020D3 RID: 8403 RVA: 0x00099A30 File Offset: 0x00097C30
		// (set) Token: 0x060020D4 RID: 8404 RVA: 0x00099A38 File Offset: 0x00097C38
		public JsonData ResponseData { get; private set; }

		// Token: 0x170005E0 RID: 1504
		// (get) Token: 0x060020D5 RID: 8405 RVA: 0x00099A44 File Offset: 0x00097C44
		// (set) Token: 0x060020D6 RID: 8406 RVA: 0x00099A4C File Offset: 0x00097C4C
		protected ApiType.Type type { get; set; }

		// Token: 0x060020D7 RID: 8407 RVA: 0x00099A58 File Offset: 0x00097C58
		public Dictionary<string, string> PrepareHeaders()
		{
			this.AddHeaderUdid();
			this.AddHeaderUserId();
			this.AddHeaderSessionId();
			this.AddHeaderParam();
			this.AddHeaderDevice();
			this.AddHeaderVersion();
			this.AddHeaderDeviceId();
			this.AddHeaderDeviceName();
			this.AddHeaderGraphicsDeviceName();
			this.AddHeaderIpAddress();
			this.AddHeaderPlatformOsVersion();
			this.AddHeaderCarrier();
			this.AddHeaderKeyChain();
			return this.header;
		}

		// Token: 0x060020D8 RID: 8408 RVA: 0x00099ABC File Offset: 0x00097CBC
		public byte[] PreparePostData()
		{
			return this.CreateBody();
		}

		// Token: 0x060020D9 RID: 8409 RVA: 0x00099AC4 File Offset: 0x00097CC4
		public void SetResponseData(JsonData data)
		{
			this.ResponseData = data;
		}

		// Token: 0x060020DA RID: 8410 RVA: 0x00099AD0 File Offset: 0x00097CD0
		public void CheckResult()
		{
			if (this.CheckCommon())
			{
				int num = this.Parse();
				if (num == 1)
				{
					if (this.CallbackOnSuccess != null)
					{
						this.CallbackOnSuccess(NetworkTask.ResultCode.Success);
					}
				}
				else if (this.CallbackOnResultCodeError != null)
				{
					this.CallbackOnResultCodeError(num);
				}
			}
		}

		// Token: 0x060020DB RID: 8411 RVA: 0x00099B2C File Offset: 0x00097D2C
		protected virtual string getUdid()
		{
			return Certification.Udid;
		}

		// Token: 0x060020DC RID: 8412 RVA: 0x00099B34 File Offset: 0x00097D34
		protected virtual byte[] CreateBody()
		{
			ObjectPacker objectPacker = new ObjectPacker();
			Debug.Log("POST data=====" + JsonMapper.ToJson(this.Params));
			byte[] inArray = objectPacker.Pack(this.Params);
			string src = Convert.ToBase64String(inArray);
			string s = CryptAES.encrypt(src);
			this.body = Encoding.UTF8.GetBytes(s);
			return this.body;
		}

		// Token: 0x060020DD RID: 8413 RVA: 0x00099B94 File Offset: 0x00097D94
		protected virtual int Parse()
		{
			return (int)this.ResponseData["data_headers"]["result_code"];
		}

		// Token: 0x060020DE RID: 8414 RVA: 0x00099BB8 File Offset: 0x00097DB8
		private void AddHeaderUdid()
		{
			string udid = this.getUdid();
			string value = Cryptographer.encode(udid);
			this.header.Add("UDID", value);
		}

		// Token: 0x060020DF RID: 8415 RVA: 0x00099BE4 File Offset: 0x00097DE4
		private void AddHeaderUserId()
		{
			string value = Cryptographer.encode(Certification.UserId.ToString());
			this.header.Add("USER_ID", value);
		}

		// Token: 0x060020E0 RID: 8416 RVA: 0x00099C18 File Offset: 0x00097E18
		private void AddHeaderSessionId()
		{
			this.header.Add("SID", Certification.SessionId);
		}

		// Token: 0x060020E1 RID: 8417 RVA: 0x00099C30 File Offset: 0x00097E30
		private void AddHeaderParam()
		{
			string udid = this.getUdid();
			int viewerId = Certification.ViewerId;
			string text = Cryptographer.generateIvString();
			string str = AES256Crypt.Encrypt(viewerId.ToString(), text);
			this.Params.viewer_id = text + str;
			ObjectPacker objectPacker = new ObjectPacker();
			byte[] inArray = objectPacker.Pack(this.Params);
			string text2 = Convert.ToBase64String(inArray);
			Uri uri = new Uri(this.Url.Trim());
			string data = string.Concat(new object[]
			{
				udid,
				Certification.ViewerId,
				uri.AbsolutePath,
				text2
			});
			string value = Cryptographer.ComputeHash(data);
			this.header.Add("PARAM", value);
		}

		// Token: 0x060020E2 RID: 8418 RVA: 0x00099CEC File Offset: 0x00097EEC
		private void AddHeaderDevice()
		{
			this.header.Add("DEVICE", Toolbox.DeviceManager.GetPlatform().ToString());
		}

		// Token: 0x060020E3 RID: 8419 RVA: 0x00099D1C File Offset: 0x00097F1C
		private void AddHeaderVersion()
		{
			this.header.Add("APP_VER", StageUtil.GetAppVersionName());
			this.header.Add("RES_VER", StageUtil.GetResourcesVersionName());
		}

		// Token: 0x060020E4 RID: 8420 RVA: 0x00099D54 File Offset: 0x00097F54
		private void AddHeaderDeviceId()
		{
			this.header.Add("DEVICE_ID", Toolbox.DeviceManager.GetDeviceUniqueIdentifier());
		}

		// Token: 0x060020E5 RID: 8421 RVA: 0x00099D70 File Offset: 0x00097F70
		private void AddHeaderDeviceName()
		{
			this.header.Add("DEVICE_NAME", Toolbox.DeviceManager.GetDeviceName());
		}

		// Token: 0x060020E6 RID: 8422 RVA: 0x00099D8C File Offset: 0x00097F8C
		private void AddHeaderGraphicsDeviceName()
		{
			this.header.Add("GRAPHICS_DEVICE_NAME", Toolbox.DeviceManager.GetGraphicsDeviceName());
		}

		// Token: 0x060020E7 RID: 8423 RVA: 0x00099DA8 File Offset: 0x00097FA8
		private void AddHeaderIpAddress()
		{
			this.header.Add("IP_ADDRESS", Toolbox.DeviceManager.GetIpAddress());
		}

		// Token: 0x060020E8 RID: 8424 RVA: 0x00099DC4 File Offset: 0x00097FC4
		private void AddHeaderPlatformOsVersion()
		{
			this.header.Add("PLATFORM_OS_VERSION", Toolbox.DeviceManager.GetOsVersion());
		}

		// Token: 0x060020E9 RID: 8425 RVA: 0x00099DE0 File Offset: 0x00097FE0
		private void AddHeaderCarrier()
		{
			this.header.Add("CARRIER", Toolbox.DeviceManager.GetCarrier());
		}

		// Token: 0x060020EA RID: 8426 RVA: 0x00099DFC File Offset: 0x00097FFC
		private void AddHeaderKeyChain()
		{
			this.header.Add("KEYCHAIN", Certification.GetKeyChainViewerId());
		}

		// Token: 0x060020EB RID: 8427 RVA: 0x00099E14 File Offset: 0x00098014
		private bool CheckCommon()
		{
			JsonData data_headers = this.ResponseData["data_headers"];
			int num = data_headers["result_code"].ToInt();
			if (num == 201)
			{
				string staticText = SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText("System0008");
				string staticText2 = SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText("System0009");
				SingletonMonoBehaviour<SceneManager>.instance.OpenErrorPopup(staticText, staticText2, Popup.eButtonType.OK, null, delegate()
				{
					ISoftwareReset.SoftwareReset();
				}, true);
				return false;
			}
			if (data_headers.Keys.Contains("sid") && data_headers["sid"] != null)
			{
				Certification.SessionId = data_headers["sid"].ToString();
			}
			if (num == 204)
			{
				string arg = string.Empty;
				arg = SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText("System0020");
				string explain = string.Format(SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText("System0006"), arg);
				string staticText3 = SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText(string.Empty);
				SingletonMonoBehaviour<SceneManager>.instance.OpenErrorPopup(staticText3, explain, Popup.eButtonType.OK, null, delegate()
				{
					Debug.Log(data_headers["store_url"].ToString());
					Application.OpenURL(data_headers["store_url"].ToString());
					Application.Quit();
				}, false);
				return false;
			}
			string value = (!data_headers.Keys.Contains("app_ver")) ? string.Empty : data_headers["app_ver"].ToString();
			Toolbox.SavedataManager.SetString("APP_VER", value);
			if (!data_headers.Keys.Contains("required_res_ver"))
			{
				return true;
			}
			LocalData.TutorialData tutorialData = SingletonMonoBehaviour<LocalData>.instance.tutorialData;
			tutorialData.Load();
			string staticText4 = SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText(string.Empty);
			string staticText5 = SingletonMonoBehaviour<MasterDataManager>.instance.GetStaticText("System0007");
			if (tutorialData.isFirstVersion)
			{
				SingletonMonoBehaviour<SceneManager>.instance.OpenErrorPopup(staticText4, staticText5, Popup.eButtonType.OK, null, delegate()
				{
					ISoftwareReset.SoftwareReset();
				}, true);
				string value2 = data_headers["required_res_ver"].ToString();
				Toolbox.SavedataManager.SetString("RES_VER", value2);
				return false;
			}
			string value3 = data_headers["required_res_ver"].ToString();
			Toolbox.SavedataManager.SetString("RES_VER", value3);
			tutorialData.isFirstVersion = true;
			tutorialData.Save();
			return true;
		}

		// Token: 0x04001515 RID: 5397
		public const int API_RESULT_SUCCESS_CODE = 1;

		// Token: 0x04001516 RID: 5398
		public const int API_RESULT_SESSION_ERROR = 201;

		// Token: 0x04001517 RID: 5399
		public const int API_RESULT_VERSION_ERROR = 204;

		// Token: 0x04001518 RID: 5400
		public PostParams Params = new PostParams();

		// Token: 0x04001519 RID: 5401
		protected Dictionary<string, string> header = new Dictionary<string, string>();

		// Token: 0x0400151A RID: 5402
		protected byte[] body;

		// Token: 0x020003AD RID: 941
		public enum ResultCode
		{
			// Token: 0x04001524 RID: 5412
			Success,
			// Token: 0x04001525 RID: 5413
			Error,
			// Token: 0x04001526 RID: 5414
			TimeOut,
			// Token: 0x04001527 RID: 5415
			DayChanged
		}
	}
}

 */
