#ifndef CrossRoad_h
#define CrossRoad_h

#include <vector>
//#include "core.hpp"
#include "opencv2/core/core.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "types.h"
#include "LinkFileInfo.hpp"
#include "NaviFile.h"

using namespace std;
//using namespace cv;

class CrossRoad
{
public:
	CrossRoad(void);
	~CrossRoad(void);

	/*
	���ܣ�
		��ȡ·������
	������
		[in]const std::vector<std::vector<HALocationCoordinate2D> >& vecMainRoadlinks - ��·link��(�ߵµ�ͼ��γ�����ݣ�
		[in]const vector<vector<LinkInfo>>& vecMainRoadLinkInfos - ��·linkInfo��		
		[in]HALocationCoordinate2D halCenterPoint - ��·���ĵ㾭γ������
		[in]cv::Size2i szCover - ���ȡ·�����ݵĳߴ緶Χ
		[in]string strDictPath - �ֵ�·��
		[in]int nCrossRoadLen - ��Ⱦʱ���Ʋ�·�ĳ���
		[out]vector<vector<HALocationCoordinate2D> >& vecCrossGpsLinks - ·�����ݣ���·����
		[out]std::vector<HALocationCoordinate2D>& vecMainRoadGpsInNet - ·����ƥ�����·
		[out]std::vector<int>& vecCrossPointIndex - ��·���·��������·�е��±�
		[out]int& nCenterIndex - ƥ�������·�е�λ��
	���أ�
		0 - ���������� - �쳣
	*/
	int getCrossLinks(const std::vector<std::vector<HALocationCoordinate2D> >& vecMainRoadGpslinks,
						const std::vector<LinkInfo>& vecMainRoadGpsLinkInfos,					
						HALocationCoordinate2D halGpsCenterPoint,
						cv::Size2i szCover,
						string strDictPath,
						int nCrossRoadLen,
						std::vector<std::vector<HALocationCoordinate2D> >& vecCrossGpsLinks,
						std::vector<HALocationCoordinate2D>& vecMainRoadGpsInNet,
						std::vector<int>& vecCrossPointIndex,
						int& nCenterIndex);

	// �����ʷ��·���
	void clearHistoryCrossPoint();


private:
	/*
	���ܣ�
		��������תgps����
	������
		[in]const std::vector<HAMapPoint> vecPixelPoint - ��������㼯
		[in]HAMapPoint hamOffset - ƫ����		
		[out]std::vector<HALocationCoordinate2D>& vecGpsPoint - ת��������
	���أ�
		0 - ���������� - �쳣
	*/
	int pixel2Gps(const std::vector<HAMapPoint> vecPixelPoint,
				HAMapPoint hamOffset,
				std::vector<HALocationCoordinate2D>& vecGpsPoint);

	/*
	���ܣ�
		��ָ���ļ����л�ȡ���Ϻ�׺Ҫ����ļ�
	������
		[in]std::string strFolder - �ļ���
		[in]std::string strSuffix - ��׺�����磺hmd��
		[out]std::vector<std::string>& vecFileNames - ���Ϻ�׺Ҫ����ļ�
	���أ�
		0 - ���������� - �쳣
	*/
	int getSuffixFiles(std::string strFolder, std::string strSuffix, std::vector<std::string>& vecFileNames);

	
	/*
	���ܣ�
		��ȡGPS���Ӧ���ֵ��ļ���
	������
		[in]const std::vector<std::string>& vecFileNames - ���Ϻ�׺Ҫ����ļ�		
		[out]std::string strDictFileName - �ֵ��ļ���
	���أ�
		true - ��ȡ�ɹ���false - ��ȡʧ��
	*/
	bool getDictFileName(HALocationCoordinate2D halGpsCenterPoint,
						const std::vector<std::string>& vecFileNames,
						std::string& strDictFileName);

	

private:
	bool m_IsReadDictionary;		// ����Ƿ��Ѷ������ֵ�
	HaloNav m_haloNav;		// ��¼�����ֵ�ӳ��
	vector<HAMapPoint> m_vecHistoryCrossPt;		// ��¼��ʷ��·���

public:
#ifdef _WINDOWS_VER_
	cv::Mat m_matImage;
#endif

};
#endif
