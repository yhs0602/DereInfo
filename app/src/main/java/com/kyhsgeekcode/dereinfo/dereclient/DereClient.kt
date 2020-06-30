package com.kyhsgeekcode.dereinfo.dereclient

import android.content.Context
import android.content.Context.CONTEXT_IGNORE_SECURITY
import android.content.SharedPreferences
import android.util.Base64
import java.nio.charset.StandardCharsets


class DereClient {
    val apiURL = "http://game.starlight-stage.jp/"
    val derePkgName = "jp.co.bandainamcoent.bnei0242"
    val encryptionKey = "e806f6"
    private val cryptoKey: String = "4441"
    fun login(context: Context) {
        val mContext: Context = context.createPackageContext(
            derePkgName,
            CONTEXT_IGNORE_SECURITY
        )
        val sharedPrefs: SharedPreferences =
            mContext.getSharedPreferences(derePkgName, Context.MODE_PRIVATE)
    }

    fun encryptDecrypt(value: String, key: String): String {
        var key = key
        if (value.isEmpty()) {
            return ""
        }
        if (key.isEmpty()) {
            key = cryptoKey
        }
        val length: Int = key.length
        val length2: Int = value.length
        val array = CharArray(length2)
        for (i in 0 until length2) {
            array[i] = (value[i].toInt() xor key[i % length].toInt()).toChar()
        }
        return String(array)
    }

    private fun encryptKey(theKey: String): String? {
        var key: String = theKey
        key = encryptDecrypt(key, encryptionKey)
        key = Base64.encodeToString(
            key.toByteArray(StandardCharsets.UTF_8),
            Base64.DEFAULT
        )
        return key
    }

//    string decryptResponseData = CryptAES.decrypt(www.text);
//    byte[] binaryData = Convert.FromBase64String(decryptResponseData);
//    IDictionary<string, object> data = this.msgPacker.Unpack(binaryData) as IDictionary<string, object>;
//    string jsonString = JsonMapper.ToJson(data);
//    Debug.Log("ResponseData:" + jsonString);
//    this.task_.SetResponseData(JsonMapper.ToObject(jsonString));
//    int resultCode = this.task_.ResponseData["data_headers"]["result_code"].ToInt();
//if (base.ResponseData["data"].Keys.Contains("user_info"))
//				{
//					JsonData jsonData2 = base.ResponseData["data"]["user_info"];
//					WorkUserData.UserData userData = SingletonMonoBehaviour<WorkDataManager>.instance.userData.userData;
//					userData.userName = jsonData2["name"].ToString();
//					userData.comment = jsonData2["comment"].ToString();
//					userData.cardMaxNum = jsonData2["max_card_num"].ToInt();
//					userData.roomStrageMaxNum = jsonData2["max_room_storage_num"].ToInt();
//					userData.friendPoint = jsonData2["friend_pt"].ToInt();
//					userData.jewelPurchase = jsonData2["jewel"].ToInt();
//					userData.jewelFree = jsonData2["free_jewel"].ToInt();
//					userData.money = jsonData2["gold"].ToInt();
//					int value = jsonData2["stamina"].ToInt();
//					userData.totalExp = jsonData2["exp"].ToInt();
//					userData.fan = jsonData2["fan"].ToInt();
//					long value2 = jsonData2["stamina_heal_time"].ToLong();
//					userData.recoveryTime = value2;
//					userData.stamina = value;
//				}
//				if (base.ResponseData["data"].Keys.Contains("user_card_list"))
//				{
//					WorkCardData cardData = SingletonMonoBehaviour<WorkDataManager>.instance.cardData;
//					JsonData jsonData3 = base.ResponseData["data"]["user_card_list"];
//					for (int i = 0; i < jsonData3.Count; i++)
//					{
//						JsonData jsonData4 = jsonData3[i];
//						int serial = (int)jsonData4["serial_id"];
//						int cardId = (int)jsonData4["card_id"];
//						WorkCardData.CardData cardData2 = cardData.AddCardData(serial);
//						cardData2.SetCardId(cardId);
//						cardData2.exp = (int)jsonData4["exp"];
//						cardData2.step = (int)jsonData4["step"];
//						cardData2.love = (int)jsonData4["love"];
//						cardData2.SetLiveSkillLevel((int)jsonData4["skill_level"]);
//						if ((int)jsonData4["protect"] == 1)
//						{
//							cardData2.isProtect = true;
//						}
//						else
//						{
//							cardData2.isProtect = false;
//						}
//					}
//				}
//				if (base.ResponseData["data"].Keys.Contains("user_unit_list"))
//				{
//					WorkUnitData unitData = SingletonMonoBehaviour<WorkDataManager>.instance.unitData;
//					TempData.LiveTempData liveTemp = SingletonMonoBehaviour<TempData>.instance.liveTemp;
//					JsonData jsonData5 = base.ResponseData["data"]["user_unit_list"];
//					JsonData jsonData6 = base.ResponseData["data"]["user_info"];
//					int num2 = jsonData6["unit_slot"].ToInt() - 1;
//					liveTemp._selectUnitId = num2;
//					for (int j = 0; j < jsonData5.Count; j++)
//					{
//						JsonData jsonData7 = jsonData5[j];
//						int key = j + 1;
//						WorkUnitData.UnitData unitData2 = unitData.dictionary[key];
//						unitData2.unitName = (string)jsonData7["name"];
//						for (int k = 0; k < 5; k++)
//						{
//							string prop_name = string.Format("serial_id_{0}", k);
//							int num3 = (int)jsonData7[prop_name];
//							unitData2.SetUnitSerial(k, num3);
//							if (num3 > 0)
//							{
//								WorkCardData.CardData cardData3 = SingletonMonoBehaviour<WorkDataManager>.instance.cardData.dictionary[(int)jsonData7[prop_name]];
//								if (k == 0)
//								{
//									if (tutorialData.step <= TutorialDefine.eTutorialStep.Evolution)
//									{
//										int value3 = cardData3.exp;
//										int value4 = cardData3.starLessonStep;
//										int value5 = cardData3.love;
//										cardData3.SetCardId(cardData3.GetSeriesId());
//										cardData3.exp = value3;
//										cardData3.step = value4;
//										cardData3.love = value5;
//									}
//									if (cardData3.GetSeriesId() == 100001)
//									{
//										tutorialData.type = 1;
//									}
//									else if (cardData3.GetSeriesId() == 200001)
//									{
//										tutorialData.type = 2;
//									}
//									else if (cardData3.GetSeriesId() == 300001)
//									{
//										tutorialData.type = 3;
//									}
//									tutorialData.Save();
//								}
//								liveTemp.SetUnitCardData(k, cardData3);
//							}
//						}
//						if (j == num2)
//						{
//							unitData2.isMain = true;
//						}
//						else
//						{
//							unitData2.isMain = false;
//						}
//					}
//				}

//    public void SetParameter()
//    {
//        LoadTaskParam loadTaskParam = new LoadTaskParam();
//        SingletonMonoBehaviour<LocalData>.instance.friendListSaveData.Load();
//        if (SingletonMonoBehaviour<LocalData>.instance.friendListSaveData.friendViewTime == long.Parse(SingletonMonoBehaviour<LocalData>.instance.friendListSaveData.DEFAULT_TIME))
//        {
//            SingletonMonoBehaviour<LocalData>.instance.friendListSaveData.SaveFriendListViewTime(TimeUtil.GetClientTimeStamp());
//        }
//        loadTaskParam.friend_view_time = SingletonMonoBehaviour<LocalData>.instance.friendListSaveData.friendViewTime;
//        loadTaskParam.tutorial_flag = (int)SingletonMonoBehaviour<LocalData>.instance.tutorialData.step;
//        loadTaskParam.live_detail_id = SingletonMonoBehaviour<LocalData>.instance.livePlay.liveDetailId;
//        loadTaskParam.live_setting = (int)SingletonMonoBehaviour<TempData>.instance.liveTemp.playModeQuality;
//        loadTaskParam.live_state = (int)SingletonMonoBehaviour<LocalData>.instance.livePlay.step;
//        LocalData.TutorialData tutorialData = SingletonMonoBehaviour<LocalData>.instance.tutorialData;
//        if (tutorialData.step == TutorialDefine.eTutorialStep.Finish && SingletonMonoBehaviour<TempData>.instance.isDayChange)
//        {
//            loadTaskParam.load_state = 1;
//        }
//        else
//        {
//            loadTaskParam.load_state = 0;
//        }
//        this.Params = loadTaskParam;
//    }

//public Dictionary<string, string> PrepareHeaders()
//		{
//			this.AddHeaderUdid();
//			this.AddHeaderUserId();
//			this.AddHeaderSessionId();
//			this.AddHeaderParam();
//			this.AddHeaderDevice();
//			this.AddHeaderVersion();
//			this.AddHeaderDeviceId();
//			this.AddHeaderDeviceName();
//			this.AddHeaderGraphicsDeviceName();
//			this.AddHeaderIpAddress();
//			this.AddHeaderPlatformOsVersion();
//			this.AddHeaderCarrier();
//			this.AddHeaderKeyChain();
//			return this.header;
//		}

//    // Token: 0x060020D8 RID: 8408 RVA: 0x00099ABC File Offset: 0x00097CBC
//    fun PreparePostData(): ByteArray? {
//        return this.CreateBody()
//    }
//
//    // Token: 0x060020D9 RID: 8409 RVA: 0x00099AC4 File Offset: 0x00097CC4
//    fun SetResponseData(data: JsonData) {
//        this.ResponseData = data
//    }

    //// Token: 0x060020DB RID: 8411 RVA: 0x00099B2C File Offset: 0x00097D2C
    //		protected virtual string getUdid()
    //		{
    //			return Certification.Udid;
    //		}
    //
    //		// Token: 0x060020DC RID: 8412 RVA: 0x00099B34 File Offset: 0x00097D34
    //		protected virtual byte[] CreateBody()
    //		{
    //			ObjectPacker objectPacker = new ObjectPacker();
    //			Debug.Log("POST data=====" + JsonMapper.ToJson(this.Params));
    //			byte[] inArray = objectPacker.Pack(this.Params);
    //			string src = Convert.ToBase64String(inArray);
    //			string s = CryptAES.encrypt(src);
    //			this.body = Encoding.UTF8.GetBytes(s);
    //			return this.body;//
//}

//    private fun encryptIntValue(key: String, value: Int): String {
//        val buffer = ByteArray(4)
//        var offset = 0
//        buffer[offset++] = value.toByte()
//        buffer[offset++] = (value shr 8).toByte()
//        buffer[offset++] = (value shr 16).toByte()
//        buffer[offset] = (value shr 24).toByte()
//        return encryptData(key, buffer, ObscuredPrefs.DataType.Int)
//    }

//    private fun encryptData(key:String, cleanBytes : ByteArray,  type : Datatype) : String
//    {
//        val num = cleanBytes.size
//        byte[] src = ObscuredPrefs.EncryptDecryptBytes(cleanBytes, num, key + ObscuredPrefs.encryptionKey);
//        uint num2 = xxHash.CalculateHash(cleanBytes, num, 0u);
//        byte[] src2 = new byte[]
//        {
//            (byte)(num2 & 255u),
//            (byte)(num2 >> 8 & 255u),
//            (byte)(num2 >> 16 & 255u),
//            (byte)(num2 >> 24 & 255u)
//        };
//        byte[] array = null;
//        int num3;
//        if (ObscuredPrefs.lockToDevice != ObscuredPrefs.DeviceLockLevel.None)
//        {
//            num3 = num + 11;
//            uint num4 = ObscuredPrefs.DeviceIDHash;
//            array = new byte[]
//            {
//                (byte)(num4 & 255u),
//                (byte)(num4 >> 8 & 255u),
//                (byte)(num4 >> 16 & 255u),
//                (byte)(num4 >> 24 & 255u)
//            };
//        }
//        else
//        {
//            num3 = num + 7;
//        }
//        byte[] array2 = new byte[num3];
//        Buffer.BlockCopy(src, 0, array2, 0, num);
//        if (array != null)
//        {
//            Buffer.BlockCopy(array, 0, array2, num, 4);
//        }
//        array2[num3 - 7] = (byte)type;
//        array2[num3 - 6] = 2;
//        array2[num3 - 5] = (byte)ObscuredPrefs.lockToDevice;
//        Buffer.BlockCopy(src2, 0, array2, num3 - 4, 4);
//        return Convert.ToBase64String(array2);
//    }
//
//    private static byte[] DecryptData(string key, string encryptedInput)
//    {
//        byte[] array;
//        try
//        {
//            array = Convert.FromBase64String(encryptedInput);
//        }
//        catch (Exception)
//        {
//            ObscuredPrefs.SavesTampered();
//            return null;
//        }
//        if (array.Length <= 0)
//        {
//            ObscuredPrefs.SavesTampered();
//            return null;
//        }
//        int num = array.Length;
//        byte b = array[num - 6];
//        if (b != 2)
//        {
//            ObscuredPrefs.SavesTampered();
//            return null;
//        }
//        ObscuredPrefs.DeviceLockLevel deviceLockLevel = (ObscuredPrefs.DeviceLockLevel)array[num - 5];
//        byte[] array2 = new byte[4];
//        Buffer.BlockCopy(array, num - 4, array2, 0, 4);
//        uint num2 = (uint)((int)array2[0] | (int)array2[1] << 8 | (int)array2[2] << 16 | (int)array2[3] << 24);
//        uint num3 = 0u;
//        int num4;
//        if (deviceLockLevel != ObscuredPrefs.DeviceLockLevel.None)
//        {
//            num4 = num - 11;
//            if (ObscuredPrefs.lockToDevice != ObscuredPrefs.DeviceLockLevel.None)
//            {
//                byte[] array3 = new byte[4];
//                Buffer.BlockCopy(array, num4, array3, 0, 4);
//                num3 = (uint)((int)array3[0] | (int)array3[1] << 8 | (int)array3[2] << 16 | (int)array3[3] << 24);
//            }
//        }
//        else
//        {
//            num4 = num - 7;
//        }
//        byte[] array4 = new byte[num4];
//        Buffer.BlockCopy(array, 0, array4, 0, num4);
//        byte[] array5 = ObscuredPrefs.EncryptDecryptBytes(array4, num4, key + ObscuredPrefs.encryptionKey);
//        uint num5 = xxHash.CalculateHash(array5, num4, 0u);
//        if (num5 != num2)
//        {
//            ObscuredPrefs.SavesTampered();
//            return null;
//        }
//        if (ObscuredPrefs.lockToDevice == ObscuredPrefs.DeviceLockLevel.Strict && num3 == 0u && !ObscuredPrefs.emergencyMode && !ObscuredPrefs.readForeignSaves)
//        {
//            return null;
//        }
//        if (num3 != 0u && !ObscuredPrefs.emergencyMode)
//        {
//            uint num6 = ObscuredPrefs.DeviceIDHash;
//            if (num3 != num6)
//            {
//                ObscuredPrefs.PossibleForeignSavesDetected();
//                if (!ObscuredPrefs.readForeignSaves)
//                {
//                    return null;
//                }
//            }
//        }
//        return array5;
//    }
}
