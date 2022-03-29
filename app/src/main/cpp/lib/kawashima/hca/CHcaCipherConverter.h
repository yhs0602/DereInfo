#pragma once

#include <map>
#include "../../cgss_data.h"
#include "CHcaFormatReader.h"

CGSS_NS_BEGIN

    class CHcaCipher;

    class CGSS_EXPORT CHcaCipherConverter : public CHcaFormatReader {

    __extends(CHcaFormatReader, CHcaFormatReader);

    public:

        /**
          * Creates a new HCA cipher converter.
          * @param stream Source stream.
          * @param cryptFrom Source cipher type.
          * @param cryptTo Wanted cipher type.
          * @return
          */
        CHcaCipherConverter(IStream *stream, const HCA_CIPHER_CONFIG &cryptFrom, const HCA_CIPHER_CONFIG &cryptTo);

        CHcaCipherConverter(const CHcaCipherConverter &) = delete;

        CHcaCipherConverter(CHcaCipherConverter &&) = delete;

        CHcaCipherConverter &operator=(const CHcaCipherConverter &) = delete;

        CHcaCipherConverter &operator=(CHcaCipherConverter &&) = delete;

        ~CHcaCipherConverter() override;

        uint32_t Read(void *buffer, uint32_t bufferSize, size_t offset, uint32_t count) override;

        uint64_t GetPosition() override;

        void SetPosition(uint64_t value) override;

        uint64_t GetLength() override;

    private:

        const uint8_t *ConvertBlock(uint32_t blockIndex);

        const uint8_t *ConvertHeader();

        void InitializeExtra();

        CHcaCipher *_cipherFrom, *_cipherTo;
        HCA_CIPHER_CONFIG _ccFrom, _ccTo;
        uint8_t *_headerBuffer;
        std::map<uint32_t, const uint8_t *> _blockBuffers;
        uint64_t _position;

    };

CGSS_NS_END
