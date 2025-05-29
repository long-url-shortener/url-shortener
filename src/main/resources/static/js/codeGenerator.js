const api = axios.create({ baseURL: '/' });

document.getElementById('shortenForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const url = document.getElementById('url').value;

    try {
        const res = await api.post('/shorten', { url });
        document.getElementById('shortUrl').textContent = res.data.shortUrl;
        document.getElementById('shortUrl').href = res.data.shortUrl;
        document.getElementById('qrImage').src = 'data:image/png;base64,' + res.data.qrBase64;
        document.getElementById('result').classList.remove('d-none');
        loadUrls();
    } catch (err) {
        alert('생성 실패: ' + err.response?.data || err.message);
    }
});

async function loadUrls() {
    const useYn = document.getElementById('useYnFilter').value;
    const res = await api.get('/admin/list', { params: { useYn } });
    const tbody = document.getElementById('urlTableBody');
    tbody.innerHTML = '';
    res.data.content.forEach(url => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
        <td>
          <a href="/${url.shortCode}" target="_blank">
            ${url.shortCode}
          </a>
        </td>
          <td><a href="${url.originalUrl}" target="_blank">${url.originalUrl}</a></td>
          <td>${url.createdAt?.substring(0, 19).replace('T', ' ')}</td>
          <td>${url.expiredAt?.substring(0, 19).replace('T', ' ') || '-'}</td>
          <td>${url.useYn === 'Y' ? '활성' : '비활성'}</td>
          <td>${url.clickCount}</td>
          <td>
            ${url.useYn === 'Y'
            ? `<button class="btn btn-sm btn-danger" onclick="deactivate('${url.shortCode}')">삭제</button>`
            : `<button class="btn btn-sm btn-success" onclick="restore('${url.shortCode}')">복구</button>`}
          </td>`;
        tbody.appendChild(tr);
    });
}

async function deactivate(code) {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    await api.patch(`/admin/urls/${code}/deactivate`);
    loadUrls();
}

async function restore(code) {
    await api.patch(`/admin/restore/${code}`);
    loadUrls();
}

document.getElementById('useYnFilter').addEventListener('change', loadUrls);
loadUrls(); // 초기 호출