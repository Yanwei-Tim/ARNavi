#include "CrossRoad.h"
#include "converter.h"
#include "NaviFile.h"
#include "MergeMapData.hpp"

#include <stdio.h>
#include <string>
#include <iostream>
#include <fstream>
#include <dirent.h>
#include <vector> 

#include <fcntl.h>
#include <sys/types.h>
#include <sys/stat.h>

using namespace std;


#ifdef _WINDOWS_VER_
	#define DIR_TYPE 0x4000
	#define FILE_TYPE 0x8000
	#define FILE_MODE 0x81B6
#else
	#define DIR_TYPE 0x0004
	#define FILE_TYPE 0x0008
	#define FILE_MODE 0x81B0
#endif

CrossRoad::CrossRoad(void)
{
	m_IsReadDictionary = false;
}


CrossRoad::~CrossRoad(void)
{
}


// ��ȡ·������
//int g_nSaveNum = 0;
int CrossRoad::getCrossLinks(const std::vector<std::vector<HALocationCoordinate2D> >& vecMainRoadGpslinks,
							 const std::vector<LinkInfo>& vecMainRoadGpsLinkInfos,					
							 HALocationCoordinate2D halGpsCenterPoint,
							 cv::Size2i szCover,
							 string strDictPath,
							 int nCrossRoadLen,
							 std::vector<std::vector<HALocationCoordinate2D> >& vecCrossGpsLinks,
							 std::vector<HALocationCoordinate2D>& vecMainRoadGpsInNet,
							 std::vector<int>& vecCrossPointIndex,
							 int& nCenterIndex)
{
	LOGD("===========getCrossLinks - enter!!=========\n");

	// �����Լ�
	int nNumLink = vecMainRoadGpslinks.size();
	if (nNumLink<=0 || nNumLink!=vecMainRoadGpsLinkInfos.size() ||
		szCover.height<=0 || szCover.width<=0)
	{
		LOGD("===========getCrossLinks - parameter Error!!=========\n");
		return -1;
	}

	#if IS_PRINT_LOG	
		LOGD("getCrossLinks - vecMainRoadGpslinks: \n");
		LOGD("	vecMainRoadGpslinks.size=%d\n", vecMainRoadGpslinks[0].size());		
		for (int i=0; i<vecMainRoadGpslinks[0].size(); i++)
		{
			LOGD("%lf, %lf;  ", vecMainRoadGpslinks[0][i].latitude, vecMainRoadGpslinks[0][i].longitude);		
		}
		LOGD("\n	szCover.w = %d, szCover.h = %d\n", szCover.width, szCover.height);	
		LOGD("halGpsCenterPoint.latitude=%lf, halGpsCenterPoint.longitude=%lf\n",halGpsCenterPoint.latitude, halGpsCenterPoint.longitude);
	#endif

	int nRet = 0;

	// ��ȡ�ֵ�����	
	if (!m_IsReadDictionary)
	{		
		nRet = confirmDict(strDictPath, halGpsCenterPoint);
		if (nRet<0)
		{
			return -1;
		}
	}
	LOGD("===========getCrossLinks - readDictionary end!!=========\n");
	
	// ����ת������·��γ��ת����
	vector<HAMapPoint> vecMainRoadPixelPt;
	HAMapPoint hamPixelCenter;
	nRet = mainGps2Pixel(vecMainRoadGpslinks,halGpsCenterPoint,vecMainRoadPixelPt,hamPixelCenter);
	if (nRet < 0)
	{
		return -1;
	}

	LOGD("===========getCrossLinks - HAMapPointForCoordinate end!!=========\n");	
		
	// ��ȡ·��link	
	std::vector<LinkInfo> vecRoadNetLinkInfo;		
	std::vector<std::vector<HAMapPoint> > vecRoadNetLink;
	nRet = m_haloNav.findLinks(hamPixelCenter,szCover.width,szCover.height,vecRoadNetLinkInfo,vecRoadNetLink);
// 	nRet = m_haloNav.findLinks(hamPixelCenter,szCover.width+2*nCrossRoadLen,szCover.height+2*nCrossRoadLen,
// 		vecRoadNetLinkInfo,vecRoadNetLink);
	if (nRet<0)
	{
		// ��ǰ�ֵ���Ч-����
		nRet = confirmDict(strDictPath, halGpsCenterPoint);
		if (nRet<0)
		{
			LOGD("getCrossLinks - no valid dict!!\n");
			return -1;
		}
		// ����ת������·��γ��ת����
		vecMainRoadPixelPt.clear();
		//HAMapPoint hamPixelCenter;
		nRet = mainGps2Pixel(vecMainRoadGpslinks,halGpsCenterPoint,vecMainRoadPixelPt,hamPixelCenter);
		if (nRet < 0)
		{
			return -1;
		}
		
		// ��ȡ·��link			
		nRet = m_haloNav.findLinks(hamPixelCenter,szCover.width,szCover.height,vecRoadNetLinkInfo,vecRoadNetLink);
		if (nRet<0)
		{
			LOGD("getCrossLinks - findLinks Error!!\n");
			return -1;
		}		
	}
	
#if IS_PRINT_LOG
	LOGD("	vecRoadNetLink.size=%d\n", vecRoadNetLink.size());	
#endif

	LOGD("===========getCrossLinks - findLinks end!!=========\n");
	
	// ��·���в�����·���ںϵ�ͼ����
	MergeMapData merMapdata;
#ifdef _WINDOWS_VER_
	merMapdata.m_matImage.create(szCover.height,szCover.width,CV_8UC3);
	int nOffsetx = hamPixelCenter.x - szCover.width/2;
	int nOffsety = hamPixelCenter.y - szCover.height/2;	
	merMapdata.m_ptOffset = cv::Point(nOffsetx, nOffsety);
#endif

#if 0		// ����·��	
	cv::Mat matRoadNetImg;
	merMapdata.drawRoadNet(vecRoadNetLink, cv::Size2i(6400,6400), hamPixelCenter, matRoadNetImg);		
// 	merMapdata.drawImage(matRoadNetImg,hamPixelCenter, vecMainRoadPixelPt, 
// 		cv::Scalar(255,0,0),50);

	char chSaveNum[20];
	itoa(g_nSaveNum++,chSaveNum,10);
	string str = "D:\\Halo\\ArWay\\output\\gimage\\roadNet\\" + string(chSaveNum) + ".bmp";
	cv::imwrite(str,matRoadNetImg);
#endif

	HAMapPoint hamCenterInNet;
	vector<LinkInfo> vecMainRoadLinkInfosInNet;
	std::vector<std::vector<HAMapPoint> > vecMainRoadLinksInNet;
	std::vector<HAMapPoint> vecMainRoadPixelInNet;
	std::vector<std::vector<HAMapPoint> > vecCrossPixelLinks;
	//std::vector<int> vecCrossPointIndex;
	int nScrW = szCover.width;
	int nScrH = szCover.height;
	cv::Rect rtScreen(hamPixelCenter.x-nScrW/2, hamPixelCenter.y-nScrH/2,nScrW,nScrH);	
	std::vector<HAMapPoint>* pVecHistoryCrossPt = &m_vecHistoryCrossPt;
	nRet = merMapdata.matchMainRoadCenterInNet5(vecMainRoadPixelPt,
												hamPixelCenter,
												vecRoadNetLinkInfo,
												vecRoadNetLink,
												rtScreen,
												nCrossRoadLen,
												hamCenterInNet,
												vecMainRoadLinkInfosInNet,
												vecMainRoadLinksInNet,
												vecMainRoadPixelInNet,
												nCenterIndex,
												vecCrossPointIndex,
												vecCrossPixelLinks,
												*pVecHistoryCrossPt);
	if (nRet<0)
	{
		#ifdef _WINDOWS_VER_
			m_matImage = merMapdata.m_matImage.clone();
		#endif
		LOGD("getCrossLinks - matchMainRoadCenterInNet5 Error!!\n");	
		return -1;
	}

#if IS_PRINT_LOG
	LOGD("	getCrossLinks - vecCrossPixelLinks\n");	
	LOGD("	getCrossLinks: vecCrossPixelLinks.size=%d\n", vecCrossPixelLinks.size());	
#endif

	LOGD("===========getCrossLinks - matchMainRoadCenterInNet end!!=========\n");

	// ����ת��������ת��γ��
	HAMapPoint hamOffset = m_haloNav.getOffset();		// ƫ����
#if 0
	int nNumRoadNetLink = vecRoadNetLink.size();
	vecCrossGpsLinks.clear();
	
	for (int i=0; i<nNumRoadNetLink; i++)
	{
		vector<HAMapPoint> vecPixelTemp = vecRoadNetLink[i];
		vector<HALocationCoordinate2D> vecGpsTemp;
		int nNumTempPt = vecPixelTemp.size();
		HALocationCoordinate2D halTempGpsPt;
		for (int j=0; j<nNumTempPt; j++)
		{
			HAMapPoint hamPixelTemp = vecPixelTemp[j];
			hamPixelTemp.x += hamOffset.x;
			hamPixelTemp.y += hamOffset.y;

			halTempGpsPt = HACoordinateForMapPoint(hamPixelTemp/*vecPixelTemp[j]*/);
			vecGpsTemp.push_back(halTempGpsPt);
		}
		vecCrossGpsLinks.push_back(vecGpsTemp);
	}
#else
	// ��·ת��
	int nCrossLinkNum = vecCrossPixelLinks.size();
	vecCrossGpsLinks.clear();
	for (int i=0; i<nCrossLinkNum; i++)
	{
		vector<HAMapPoint> vecPixelTemp = vecCrossPixelLinks[i];
		vector<HALocationCoordinate2D> vecGpsTemp;
		
		nRet = pixel2Gps(vecPixelTemp,	hamOffset, vecGpsTemp);
		if (nRet < 0)
		{
			LOGD("getCrossLinks - crossRoad pixel2Gps Error!!\n");	
			return -1;
		}
		
		vecCrossGpsLinks.push_back(vecGpsTemp);
	}

	// ��·ת��
	nRet = pixel2Gps(vecMainRoadPixelInNet,	hamOffset, vecMainRoadGpsInNet);
	if (nRet < 0)
	{
		LOGD("getCrossLinks - mainRoad pixel2Gps Error!!\n");	
		return -1;
	}
#endif

#ifdef _WINDOWS_VER_
	m_matImage = merMapdata.m_matImage.clone();
#endif

#if IS_PRINT_LOG
	LOGD("	getCrossLinks: vecCrossGpsLinks.size=%d, vecMainRoadGpsInNet.size=%d\n",
		vecCrossGpsLinks.size(), vecMainRoadGpsInNet.size());
#endif

	LOGD("===========getCrossLinks - leave!!=========\n\n");
	LOGD("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
	return 0;
}

// ȷ���ֵ�
int CrossRoad::confirmDict(std::string strDictPath, HALocationCoordinate2D halGpsCenterPoint)
{
	// ��ָ��·���л�ȡ���Ϻ�׺Ҫ��������ļ�
	std::string strSuffix = ".hmd";
	std::vector<std::string> vecFileNames;
	int nRet = getSuffixFiles(strDictPath, strSuffix, vecFileNames);
	if (nRet<0)
	{		
		LOGD("getCrossLinks - getSuffixFiles Error!!\n");		
		return -1;
	}
#if IS_PRINT_LOG
	LOGD("getCrossLinks - getSuffixFiles: \n");
	for (int i=0; i<vecFileNames.size(); i++)
	{
		LOGD("	vecFileNames[%d] - %s\n",i,vecFileNames[i].c_str());
	}
#endif

	// ȷ���ֵ��ļ�
	std::string strDictFileName;
	if (!getDictFileName(halGpsCenterPoint,	vecFileNames, strDictFileName))
	{		
		LOGD("getCrossLinks - getDictFileName Error!!\n");		
		return -1;
	}		
#if IS_PRINT_LOG
	LOGD("getCrossLinks - getDictFileName: strDictFileName = %s\n", strDictFileName.c_str());			
#endif

	nRet = m_haloNav.readDictionary(strDictFileName);
	if (nRet<0)
	{		
		LOGD("getCrossLinks - readDictionary Error!!\n");		
		return -1;
	}
	m_IsReadDictionary = true;

	return 0;
}

// ��·��γ��ת����



// ����ת������·��γ��ת����
int CrossRoad::mainGps2Pixel(const std::vector<std::vector<HALocationCoordinate2D> >& vecMainRoadGpslinks,		// ��·gps��
							 HALocationCoordinate2D halGpsCenterPoint,		// ���ĵ�gps
							 vector<HAMapPoint>& vecMainRoadPixelPt,		// ��·��������
							 HAMapPoint& hamPixelCenter)		// ���ĵ���������
{
	int nNumLink = vecMainRoadGpslinks.size();

	// �����Լ�
	if (nNumLink<=0)
	{
		return -1;
	}

	// ����ת������·��γ��ת����
	HAMapPoint hamOffset = m_haloNav.getOffset();		// ƫ����
	vector<vector<HAMapPoint> > vecMainRoadPixelLinks;
	/*vector<HAMapPoint> vecMainRoadPixelPt;*/
	for (int i=0; i<nNumLink; i++)
	{
		vector<HALocationCoordinate2D> vecLink = vecMainRoadGpslinks[i];
		int nNumPt = vecLink.size();
		HAMapPoint hamPixelXY;
		vector<HAMapPoint> vecTemp;
		for (int j=0; j<nNumPt; j++)
		{			
			hamPixelXY = HAMapPointForCoordinate(vecLink[j]);
			hamPixelXY.x -= hamOffset.x;		// ��ȥƫ����
			hamPixelXY.y -= hamOffset.y;
			vecTemp.push_back(hamPixelXY);
			vecMainRoadPixelPt.push_back(hamPixelXY);
		}
		vecMainRoadPixelLinks.push_back(vecTemp);
	}

	// ���ĵ�
	hamPixelCenter = HAMapPointForCoordinate(halGpsCenterPoint);
	hamPixelCenter.x -= hamOffset.x;
	hamPixelCenter.y -= hamOffset.y;

	return 0;
}

int CrossRoad::pixel2Gps(const std::vector<HAMapPoint>& vecPixelPoint,
						 HAMapPoint hamOffset,
						 std::vector<HALocationCoordinate2D>& vecGpsPoint)
{
	// �����Լ�
	int nNumPt = vecPixelPoint.size();
	if (nNumPt<=0)
	{
		return -1;
	}

	HALocationCoordinate2D halTempGpsPt;
	for (int i=0; i<nNumPt; i++)
	{
		HAMapPoint hamPixelTemp = vecPixelPoint[i];
		hamPixelTemp.x += hamOffset.x;
		hamPixelTemp.y += hamOffset.y;

		halTempGpsPt = HACoordinateForMapPoint(hamPixelTemp/*vecPixelTemp[j]*/);
		vecGpsPoint.push_back(halTempGpsPt);
	}

	return 0;
}

// ��ָ���ļ����л�ȡ���Ϻ�׺Ҫ����ļ�
int CrossRoad::getSuffixFiles(std::string strFolder, std::string strSuffix, std::vector<std::string>& vecFileNames)
{
	// �������·�������ļ���ʱ���磺..\\..\\xx.hmd��ȥ���ļ�������ȡ·��
	struct stat st;	
	stat(strFolder.c_str(), &st);     //���� �ļ�, windows - 33206, android - 33200

	LOGD("getSuffixFiles st.st_mode=%d\n",st.st_mode);

	if (st.st_mode==FILE_MODE)
	{
 		vecFileNames.push_back(strFolder);
 		return 0;
		/*int nSi = -1;
		#ifdef _WINDOWS_VER_
			nSi = strFolder.find_last_of("\\");
		#else
			nSi = strFolder.find_last_of("/");
		#endif
		
		strFolder = strFolder.substr(0,nSi);*/
	} 
	
	LOGD("getSuffixFiles strFolder=%s\n",strFolder.c_str());

	
	DIR * pDir = NULL;
	struct dirent *ent = NULL;
	pDir = opendir(strFolder.c_str());
	if (NULL == pDir)
	{
		LOGD("getSuffixFiles opendir==Null\n");
		return -1;
	}
	while (NULL != (ent = readdir (pDir)))
	{		
		if ((ent->d_type==FILE_TYPE) && strstr(ent->d_name, strSuffix.c_str()) != NULL) 
		{
			string str;
		#ifdef _WINDOWS_VER_
			str = strFolder + "\\" + ent->d_name;		// ����·�����ļ���
		#else
			str = strFolder + "//" + ent->d_name;		// ����·�����ļ���
		#endif			
			vecFileNames.push_back(str);
		}		
	}
	closedir (pDir);
	
	if (vecFileNames.size()>0)
	{
		return 0;
	}
	else
	{
		return -1;
	}
}

// ��ȡGPS���Ӧ���ֵ��ļ���
bool CrossRoad::getDictFileName(HALocationCoordinate2D halGpsCenterPoint,
					const std::vector<std::string>& vecFileNames,
					std::string& strDictFileName)
{
	// �����Լ�
	int nNum = vecFileNames.size();
	if (nNum<=0)
	{
		return false;
	}

	int nRet = -1;
	bool bIsGot = false;		// ��ʶ�Ƿ��ҵ��ֵ��ļ�
	HaloNav haloNav;
	MergeMapData mergeData;
	for (int i=0; i<nNum; i++)
	{	
		// ���ֵ�
		string strFname = vecFileNames[i];
		nRet = haloNav.readDictionary(strFname);
		if (nRet<0)
		{
			continue;
		}
		
		// ��ȡƫ�����Ϳ��߷����block����
		HAMapPoint hamOffset = haloNav.getOffset();		// ƫ����
		int nWBlockNum = 0, nHBlockNum = 0;
		haloNav.getBlocksSize(nWBlockNum, nHBlockNum);

		// ����ת������γ��ת����
		HAMapPoint hamPixelXY = HAMapPointForCoordinate(halGpsCenterPoint);

		// �ж������Ƿ�λ�ھ��ο���
		cv::Rect rt(hamOffset.x, hamOffset.y, nWBlockNum*BLOCK_WIDTH, nHBlockNum*BLOCK_HEIGH);
		if (mergeData.isRectInside(hamPixelXY,rt))
		{
			strDictFileName = vecFileNames[i];
			bIsGot = true;
			break;
		}		
	}

	return bIsGot;
}

// �����ʷ��·���
void CrossRoad::clearHistoryCrossPoint()
{
	m_vecHistoryCrossPt.clear();
}

// ���ƴ�ӡlog

