//
// Created by 양현서 on 2022/03/29.
//

#ifndef DEREINFO_CGSSH_H
#define DEREINFO_CGSSH_H

#include <stdint.h>

#ifdef __COMPILE_WITH_CGSS_KEYS
static const uint32_t g_CgssKey1 = 0xF27E3B22;
static const uint32_t g_CgssKey2 = 0x00003657;
#else
static const uint32_t g_CgssKey1 = 0;
static const uint32_t g_CgssKey2 = 0;
#endif

#endif //DEREINFO_CGSSH_H
